package com.example.data.repository

import com.example.data.UserSettings
import com.example.data.dao.UserSettingsDao
import kotlinx.coroutines.flow.Flow

class UserSettingsRepository(private val userSettingsDao: UserSettingsDao) {
    val settings: Flow<UserSettings?> = userSettingsDao.getUserSettings()

    suspend fun getSettingsDirect(): UserSettings {
        return userSettingsDao.getUserSettingsDirect() ?: UserSettings()
    }

    suspend fun updateSettings(userSettings: UserSettings) {
        userSettingsDao.insertOrUpdateSettings(userSettings)
    }
}
