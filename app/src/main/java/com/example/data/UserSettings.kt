package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 1,
    val isBiometricEnabled: Boolean = false,
    val pinCode: String = "1234",
    val themeMode: String = "SYSTEM", // LIGHT, DARK, SYSTEM
    val themePalette: String = "WARM_ROSE", // WARM_ROSE, AMBER_SUN, MATCHA_GREEN, MORANDI_BLUE, LAVENDER_PURPLE, CARAMEL_COFFEE, SAKURA_PINK
    val homeBannerUri: String = "",
    val nickname: String = "暖暖的记录者",
    val bio: String = "慢下来，感受生活的微光与温柔。",
    val avatarUrl: String = "",
    val userId: String = "warm_me_666",
    val isAutoSyncEnabled: Boolean = true,
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)
