package com.example.ui.viewmodel

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.UserSettings
import com.example.data.repository.DiaryRepository
import com.example.data.repository.UserSettingsRepository
import com.example.data.security.BiometricLockManager
import com.example.data.sync.CloudSyncManager
import com.example.data.sync.SyncStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavigationTab {
    DIARY,
    CALENDAR,
    COMMUNITY,
    FRIENDS,
    SETTINGS
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val userSettingsRepo = UserSettingsRepository(db.userSettingsDao())
    private val diaryRepo = DiaryRepository(db.diaryDao())

    val biometricLockManager = BiometricLockManager(application)
    val cloudSyncManager = CloudSyncManager(diaryRepo, userSettingsRepo)

    val userSettings: StateFlow<UserSettings?> = userSettingsRepo.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _selectedTab = MutableStateFlow(NavigationTab.DIARY)
    val selectedTab: StateFlow<NavigationTab> = _selectedTab.asStateFlow()

    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    val syncStatus: StateFlow<SyncStatus> = cloudSyncManager.syncStatus

    init {
        viewModelScope.launch {
            userSettingsRepo.settings.collect { settings ->
                if (settings != null && settings.isBiometricEnabled) {
                    // Lock app if privacy lock is enabled and hasn't been unlocked yet
                    _isLocked.value = true
                } else {
                    _isLocked.value = false
                }
            }
        }
    }

    fun selectTab(tab: NavigationTab) {
        _selectedTab.value = tab
    }

    fun unlockWithPin(inputPin: String): Boolean {
        val currentSettings = userSettings.value ?: return false
        return if (inputPin == currentSettings.pinCode) {
            _isLocked.value = false
            true
        } else {
            false
        }
    }

    fun unlockWithBiometrics(activity: FragmentActivity, onError: (String) -> Unit) {
        biometricLockManager.authenticate(
            activity = activity,
            title = "验证指纹解锁暖记",
            subtitle = "使用指纹或面容识别保护个人私密日记",
            onSuccess = {
                _isLocked.value = false
            },
            onError = { err ->
                onError(err)
            },
            onUsePinFallback = {
                // PIN code keypad is available in LockOverlayScreen
            }
        )
    }

    fun lockApp() {
        _isLocked.value = true
    }

    fun updateThemeMode(mode: String) {
        viewModelScope.launch {
            val current = userSettingsRepo.getSettingsDirect()
            userSettingsRepo.updateSettings(current.copy(themeMode = mode))
        }
    }

    fun updateThemePalette(palette: String) {
        viewModelScope.launch {
            val current = userSettingsRepo.getSettingsDirect()
            userSettingsRepo.updateSettings(current.copy(themePalette = palette))
        }
    }

    fun updateHomeBanner(bannerUri: String) {
        viewModelScope.launch {
            val current = userSettingsRepo.getSettingsDirect()
            userSettingsRepo.updateSettings(current.copy(homeBannerUri = bannerUri))
        }
    }

    fun toggleBiometricLock(enabled: Boolean, pin: String = "1234") {
        viewModelScope.launch {
            val current = userSettingsRepo.getSettingsDirect()
            userSettingsRepo.updateSettings(
                current.copy(
                    isBiometricEnabled = enabled,
                    pinCode = if (pin.isNotBlank()) pin else current.pinCode
                )
            )
            if (!enabled) {
                _isLocked.value = false
            }
        }
    }

    fun updateAvatarUrl(avatarUrl: String) {
        viewModelScope.launch {
            val current = userSettingsRepo.getSettingsDirect()
            userSettingsRepo.updateSettings(current.copy(avatarUrl = avatarUrl))
        }
    }

    fun updateProfile(nickname: String, bio: String, avatarUrl: String? = null) {
        viewModelScope.launch {
            val current = userSettingsRepo.getSettingsDirect()
            userSettingsRepo.updateSettings(
                current.copy(
                    nickname = nickname,
                    bio = bio,
                    avatarUrl = avatarUrl ?: current.avatarUrl
                )
            )
        }
    }

    fun triggerCloudSync(username: String? = null) {
        viewModelScope.launch {
            cloudSyncManager.triggerCloudSync(username)
        }
    }
}
