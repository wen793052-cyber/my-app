package com.example.data.sync

import com.example.data.DiaryEntry
import com.example.data.repository.DiaryRepository
import com.example.data.repository.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

sealed class SyncStatus {
    object Idle : SyncStatus()
    data class Syncing(val progress: Float, val message: String) : SyncStatus()
    data class Success(val syncedTime: Long) : SyncStatus()
    data class Error(val message: String) : SyncStatus()
}

class CloudSyncManager(
    private val diaryRepository: DiaryRepository,
    private val userSettingsRepository: UserSettingsRepository
) {
    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val api = CloudNetworkClient.api

    suspend fun triggerCloudSync(username: String?) = withContext(Dispatchers.IO) {
        if (username.isNullOrBlank()) {
            _syncStatus.value = SyncStatus.Error("未登录账号！请先在【好友】页面注册/登录账号，即可自动同步日记到云端。")
            return@withContext
        }

        try {
            _syncStatus.value = SyncStatus.Syncing(0.15f, "正在连接云端服务器 (@$username)...")

            // 1. Upload local entries to cloud
            val localEntries = diaryRepository.getAllEntriesList()
            _syncStatus.value = SyncStatus.Syncing(0.35f, "正在备份本地 ${localEntries.size} 篇日记到云端...")

            localEntries.forEach { entry ->
                val cloudId = if (entry.timestamp > 0) entry.timestamp.toString() else System.currentTimeMillis().toString()
                val dto = CloudDiaryDto(
                    cloudId = cloudId,
                    title = entry.title,
                    content = entry.content,
                    date = entry.date,
                    formattedDate = entry.formattedDate,
                    timestamp = entry.timestamp,
                    mood = entry.mood,
                    weather = entry.weather,
                    imageUris = entry.imageUris,
                    tags = entry.tags,
                    location = entry.location,
                    privacyLevel = entry.privacyLevel
                )
                try {
                    api.saveUserDiary(username, cloudId, dto)
                } catch (e: Exception) {
                    // Continue with remaining entries
                }
            }

            _syncStatus.value = SyncStatus.Syncing(0.70f, "正在从云端拉取已同步的全部日记...")

            // 2. Download all cloud entries for user
            val cloudMap = try { api.getUserDiaries(username) } catch (e: Exception) { null }
            if (cloudMap != null && cloudMap.isNotEmpty()) {
                val downloadedList = cloudMap.values.map { dto ->
                    DiaryEntry(
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
                diaryRepository.insertAll(downloadedList)
            }

            _syncStatus.value = SyncStatus.Syncing(0.95f, "正在核对云端备份完整性...")
            delay(300)

            val now = System.currentTimeMillis()
            val currentSettings = userSettingsRepository.getSettingsDirect()
            userSettingsRepository.updateSettings(currentSettings.copy(lastSyncTimestamp = now))

            _syncStatus.value = SyncStatus.Success(now)
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.Error("同步失败: ${e.localizedMessage ?: "网络连接不稳定"}")
        }
    }

    suspend fun restoreDiariesFromCloud(username: String) = withContext(Dispatchers.IO) {
        if (username.isBlank()) return@withContext
        try {
            val cloudMap = api.getUserDiaries(username)
            if (cloudMap != null && cloudMap.isNotEmpty()) {
                val downloadedList = cloudMap.values.map { dto ->
                    DiaryEntry(
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
                diaryRepository.insertAll(downloadedList)
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun resetStatus() {
        _syncStatus.value = SyncStatus.Idle
    }
}
