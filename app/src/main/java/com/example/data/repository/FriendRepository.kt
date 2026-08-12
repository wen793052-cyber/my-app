package com.example.data.repository

import com.example.data.Friend
import com.example.data.dao.FriendDao
import kotlinx.coroutines.flow.Flow

class FriendRepository(private val friendDao: FriendDao) {
    val allFriends: Flow<List<Friend>> = friendDao.getAllFriends()

    suspend fun addFriend(friend: Friend): Long {
        return friendDao.insertFriend(friend)
    }

    suspend fun deleteFriend(id: Long) {
        friendDao.deleteFriend(id)
    }
}
