package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CommunityComment
import com.example.data.CommunitySnippet
import com.example.data.Friend
import com.example.data.repository.AuthResult
import com.example.data.repository.CloudAuthAndFriendRepository
import com.example.data.repository.CommunityRepository
import com.example.data.repository.DiaryRepository
import com.example.data.repository.FriendRepository
import com.example.data.repository.UserSettingsRepository
import com.example.data.sync.FriendRequestDto
import com.example.data.sync.UserProfileDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommunityViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val communityRepo = CommunityRepository(db.communityDao())
    private val friendRepo = FriendRepository(db.friendDao())
    private val userSettingsRepo = UserSettingsRepository(db.userSettingsDao())
    private val diaryRepo = DiaryRepository(db.diaryDao())

    val cloudAuthRepo = CloudAuthAndFriendRepository(db.friendDao(), userSettingsRepo, diaryRepo)

    val currentUser: StateFlow<UserProfileDto?> = cloudAuthRepo.currentUser
    val incomingRequests: StateFlow<List<FriendRequestDto>> = cloudAuthRepo.incomingRequests

    val allSnippets: StateFlow<List<CommunitySnippet>> = communityRepo.allSnippets.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allFriends: StateFlow<List<Friend>> = friendRepo.allFriends.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _activeSnippetForComments = MutableStateFlow<CommunitySnippet?>(null)
    val activeSnippetForComments: StateFlow<CommunitySnippet?> = _activeSnippetForComments.asStateFlow()

    private val _commentsForActiveSnippet = MutableStateFlow<List<CommunityComment>>(emptyList())
    val commentsForActiveSnippet: StateFlow<List<CommunityComment>> = _commentsForActiveSnippet.asStateFlow()

    fun registerAccount(username: String, password: String, nickname: String, onResult: (AuthResult) -> Unit) {
        viewModelScope.launch {
            val res = cloudAuthRepo.registerAccount(username, password, nickname)
            onResult(res)
        }
    }

    fun loginAccount(username: String, password: String, onResult: (AuthResult) -> Unit) {
        viewModelScope.launch {
            val res = cloudAuthRepo.loginAccount(username, password)
            onResult(res)
        }
    }

    fun sendFriendRequest(targetUsername: String, message: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val msg = cloudAuthRepo.sendFriendRequest(targetUsername, message)
            onResult(msg)
        }
    }

    fun refreshRequests() {
        viewModelScope.launch {
            cloudAuthRepo.refreshIncomingRequests()
        }
    }

    fun acceptRequest(request: FriendRequestDto, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val msg = cloudAuthRepo.acceptFriendRequest(request)
            onResult(msg)
        }
    }

    fun rejectRequest(request: FriendRequestDto) {
        viewModelScope.launch {
            cloudAuthRepo.rejectFriendRequest(request)
        }
    }

    fun logout() {
        cloudAuthRepo.logout()
    }

    fun toggleLike(snippet: CommunitySnippet) {
        viewModelScope.launch {
            communityRepo.toggleLike(snippet)
        }
    }

    fun openComments(snippet: CommunitySnippet) {
        _activeSnippetForComments.value = snippet
        viewModelScope.launch {
            communityRepo.getCommentsForSnippet(snippet.id).collect { comments ->
                _commentsForActiveSnippet.value = comments
            }
        }
    }

    fun closeComments() {
        _activeSnippetForComments.value = null
        _commentsForActiveSnippet.value = emptyList()
    }

    fun addComment(text: String) {
        val snippet = activeSnippetForComments.value ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            val user = userSettingsRepo.getSettingsDirect()
            val comment = CommunityComment(
                snippetId = snippet.id,
                authorName = user.nickname,
                commentText = text,
                timestamp = System.currentTimeMillis()
            )
            communityRepo.addComment(comment)
        }
    }

    fun publishSnippet(title: String, excerpt: String, mood: String, imageUrl: String) {
        viewModelScope.launch {
            val user = userSettingsRepo.getSettingsDirect()
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            communityRepo.publishSnippet(
                CommunitySnippet(
                    authorId = user.userId,
                    authorName = user.nickname,
                    title = title,
                    excerpt = excerpt,
                    mood = mood,
                    imageUrl = imageUrl,
                    publishDate = dateStr
                )
            )
        }
    }

    fun addFriend(friendId: String, name: String, bio: String) {
        viewModelScope.launch {
            friendRepo.addFriend(
                Friend(
                    friendId = friendId.ifBlank { "warm_friend_${(100..999).random()}" },
                    name = name.ifBlank { "暖记新好友" },
                    bio = bio.ifBlank { "用文字记录温暖生活~" },
                    status = "ACCEPTED"
                )
            )
        }
    }

    fun removeFriend(id: Long) {
        viewModelScope.launch {
            friendRepo.deleteFriend(id)
        }
    }
}

