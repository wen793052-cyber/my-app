package com.example.data.repository

import com.example.data.Friend
import com.example.data.dao.FriendDao
import com.example.data.sync.CloudFriendDto
import com.example.data.sync.CloudNetworkClient
import com.example.data.sync.FriendRequestDto
import com.example.data.sync.UserProfileDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

sealed class AuthResult {
    data class Success(val userProfile: UserProfileDto) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class CloudAuthAndFriendRepository(
    private val friendDao: FriendDao,
    private val userSettingsRepository: UserSettingsRepository,
    private val diaryRepository: DiaryRepository
) {
    private val api = CloudNetworkClient.api

    private val _currentUser = MutableStateFlow<UserProfileDto?>(null)
    val currentUser: StateFlow<UserProfileDto?> = _currentUser.asStateFlow()

    private val _incomingRequests = MutableStateFlow<List<FriendRequestDto>>(emptyList())
    val incomingRequests: StateFlow<List<FriendRequestDto>> = _incomingRequests.asStateFlow()

    suspend fun registerAccount(username: String, password: String, nickname: String, avatarUrl: String = ""): AuthResult = withContext(Dispatchers.IO) {
        try {
            val cleanUsername = username.trim().lowercase()
            if (cleanUsername.length < 3) {
                return@withContext AuthResult.Error("用户名长度至少需要 3 个字符")
            }
            if (password.length < 4) {
                return@withContext AuthResult.Error("密码长度至少需要 4 位")
            }

            // Check if username already exists
            val existing = try { api.getUserProfile(cleanUsername) } catch (e: Exception) { null }
            if (existing != null && existing.username.isNotBlank()) {
                return@withContext AuthResult.Error("该用户名已经被注册，请换一个试试")
            }

            val profile = UserProfileDto(
                username = cleanUsername,
                password = password,
                nickname = if (nickname.isBlank()) "暖心日记手 $cleanUsername" else nickname,
                avatarUrl = avatarUrl,
                bio = "用文字记录生活的温暖细节✨"
            )

            api.saveUserProfile(cleanUsername, profile)
            _currentUser.value = profile

            // Update local UserSettings
            val settings = userSettingsRepository.getSettingsDirect()
            userSettingsRepository.updateSettings(
                settings.copy(
                    nickname = profile.nickname,
                    avatarUrl = profile.avatarUrl,
                    bio = profile.bio
                )
            )

            AuthResult.Success(profile)
        } catch (e: Exception) {
            AuthResult.Error("注册失败，请检查网络连接: ${e.localizedMessage}")
        }
    }

    suspend fun loginAccount(username: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            val cleanUsername = username.trim().lowercase()
            val profile = api.getUserProfile(cleanUsername)
            if (profile == null || profile.username.isBlank()) {
                return@withContext AuthResult.Error("该用户名不存在，请先注册")
            }
            if (profile.password != password) {
                return@withContext AuthResult.Error("密码错误，请重新输入")
            }

            _currentUser.value = profile

            // Update local UserSettings
            val settings = userSettingsRepository.getSettingsDirect()
            userSettingsRepository.updateSettings(
                settings.copy(
                    nickname = profile.nickname,
                    avatarUrl = profile.avatarUrl,
                    bio = profile.bio
                )
            )

            // Sync cloud friends and restore cloud diaries for this user down to local database
            syncFriendsFromCloud(cleanUsername)
            restoreDiariesFromCloud(cleanUsername)

            AuthResult.Success(profile)
        } catch (e: Exception) {
            AuthResult.Error("登录失败: ${e.localizedMessage ?: "网络不可用"}")
        }
    }

    suspend fun searchUser(username: String): UserProfileDto? = withContext(Dispatchers.IO) {
        try {
            val cleanUsername = username.trim().lowercase()
            val profile = api.getUserProfile(cleanUsername)
            if (profile != null && profile.username.isNotBlank()) profile else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun sendFriendRequest(targetUsername: String, message: String): String = withContext(Dispatchers.IO) {
        try {
            val current = _currentUser.value ?: return@withContext "请先登录账号"
            val targetClean = targetUsername.trim().lowercase()
            if (current.username == targetClean) {
                return@withContext "不能添加自己为好友哦"
            }

            val targetUser = searchUser(targetClean) ?: return@withContext "未找到该用户名，请确认是否拼写正确"

            val requestId = "${current.username}_${System.currentTimeMillis()}"
            val request = FriendRequestDto(
                requestId = requestId,
                fromUsername = current.username,
                fromNickname = current.nickname,
                fromAvatarUrl = current.avatarUrl,
                toUsername = targetClean,
                message = if (message.isBlank()) "想和你一起互相鼓励记日记~" else message,
                status = "PENDING",
                timestamp = System.currentTimeMillis()
            )

            api.sendFriendRequest(targetClean, requestId, request)
            "好友申请已发送给【${targetUser.nickname}】，等待对方通过"
        } catch (e: Exception) {
            "发送失败: ${e.localizedMessage ?: "网络连接异常"}"
        }
    }

    suspend fun refreshIncomingRequests() = withContext(Dispatchers.IO) {
        try {
            val current = _currentUser.value ?: return@withContext
            val map = api.getIncomingRequests(current.username)
            if (map != null) {
                _incomingRequests.value = map.values.filter { it.status == "PENDING" }
            } else {
                _incomingRequests.value = emptyList()
            }
        } catch (e: Exception) {
            // Ignore error
        }
    }

    suspend fun acceptFriendRequest(request: FriendRequestDto): String = withContext(Dispatchers.IO) {
        try {
            val current = _currentUser.value ?: return@withContext "请先登录账号"

            // 1. Add relation for current -> fromUser
            val friend1 = CloudFriendDto(
                friendUsername = request.fromUsername,
                nickname = request.fromNickname,
                avatarUrl = request.fromAvatarUrl,
                bio = "暖记好友",
                addedAt = System.currentTimeMillis()
            )
            api.addFriendRelation(current.username, request.fromUsername, friend1)

            // 2. Add relation for fromUser -> current
            val friend2 = CloudFriendDto(
                friendUsername = current.username,
                nickname = current.nickname,
                avatarUrl = current.avatarUrl,
                bio = "暖记好友",
                addedAt = System.currentTimeMillis()
            )
            api.addFriendRelation(request.fromUsername, current.username, friend2)

            // 3. Delete the request
            api.deleteFriendRequest(current.username, request.requestId)

            // 4. Save to local Room DB
            friendDao.insertFriend(
                Friend(
                    friendId = request.fromUsername,
                    name = request.fromNickname,
                    avatarUrl = request.fromAvatarUrl,
                    bio = "暖记好友",
                    status = "ACCEPTED"
                )
            )

            refreshIncomingRequests()
            syncFriendsFromCloud(current.username)

            "已同意【${request.fromNickname}】的好友申请！"
        } catch (e: Exception) {
            "操作失败: ${e.localizedMessage}"
        }
    }

    suspend fun rejectFriendRequest(request: FriendRequestDto) = withContext(Dispatchers.IO) {
        try {
            val current = _currentUser.value ?: return@withContext
            api.deleteFriendRequest(current.username, request.requestId)
            refreshIncomingRequests()
        } catch (e: Exception) {
            // Ignore
        }
    }

    suspend fun syncFriendsFromCloud(username: String) = withContext(Dispatchers.IO) {
        try {
            val map = api.getFriendsList(username)
            if (map != null) {
                val list = map.values.map { dto ->
                    Friend(
                        friendId = dto.friendUsername,
                        name = dto.nickname,
                        avatarUrl = dto.avatarUrl,
                        bio = dto.bio,
                        status = "ACCEPTED",
                        addedTimestamp = dto.addedAt
                    )
                }
                friendDao.insertAll(list)
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    suspend fun restoreDiariesFromCloud(username: String) = withContext(Dispatchers.IO) {
        try {
            val map = api.getUserDiaries(username)
            if (map != null && map.isNotEmpty()) {
                val list = map.values.map { dto ->
                    com.example.data.DiaryEntry(
                        title = dto.title,
                        content = dto.content,
                        date = dto.date,
                        formattedDate = dto.formattedDate,
                        timestamp = dto.timestamp,
                        mood = dto.mood,
                        weather = dto.weather,
                        imageUris = dto.imageUris,
                        tags = dto.tags,
                        location = dto.location,
                        privacyLevel = dto.privacyLevel,
                        isSynced = true
                    )
                }
                diaryRepository.insertAll(list)
            }
        } catch (e: Exception) {
            // Ignore errors
        }
    }

    fun logout() {
        _currentUser.value = null
        _incomingRequests.value = emptyList()
    }
}
