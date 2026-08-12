package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "community_snippets")
data class CommunitySnippet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String = "",
    val diaryId: Long = 0,
    val title: String,
    val excerpt: String,
    val imageUrl: String = "",
    val mood: String = "🌤️ 舒畅",
    val publishDate: String, // e.g. "2026-08-11"
    val timestamp: Long = System.currentTimeMillis(),
    val likeCount: Int = 0,
    val isLikedByMe: Boolean = false
)

@Entity(tableName = "community_comments")
data class CommunityComment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val snippetId: Long,
    val authorName: String,
    val authorAvatar: String = "",
    val commentText: String,
    val timestamp: Long = System.currentTimeMillis()
)
