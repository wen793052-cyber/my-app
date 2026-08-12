package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val date: String, // Format: YYYY-MM-DD
    val formattedDate: String, // Format: 2026年8月11日 星期二
    val timestamp: Long = System.currentTimeMillis(),
    val mood: String = "😊 充实", // Mood string with emoji
    val weather: String = "☀️ 晴朗", // Weather string with emoji
    val imageUris: String = "", // Comma-separated list of image URIs or resource names
    val tags: String = "", // Comma-separated tags, e.g., "#日常生活,#记录"
    val location: String = "",
    val isSharedToCommunity: Boolean = false,
    val privacyLevel: String = "PRIVATE", // PRIVATE, FRIENDS, PUBLIC
    val isSynced: Boolean = false
)
