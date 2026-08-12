package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class Friend(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val friendId: String, // e.g. "warm_user_88"
    val name: String,
    val avatarUrl: String = "", // URI or placeholder tag
    val bio: String = "用文字记录温暖的生活片段~",
    val status: String = "ACCEPTED", // ACCEPTED, PENDING
    val addedTimestamp: Long = System.currentTimeMillis()
)
