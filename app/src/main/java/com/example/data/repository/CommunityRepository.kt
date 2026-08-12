package com.example.data.repository

import com.example.data.CommunityComment
import com.example.data.CommunitySnippet
import com.example.data.dao.CommunityDao
import kotlinx.coroutines.flow.Flow

class CommunityRepository(private val communityDao: CommunityDao) {
    val allSnippets: Flow<List<CommunitySnippet>> = communityDao.getAllSnippets()

    fun getCommentsForSnippet(snippetId: Long): Flow<List<CommunityComment>> {
        return communityDao.getCommentsForSnippet(snippetId)
    }

    suspend fun publishSnippet(snippet: CommunitySnippet): Long {
        return communityDao.insertSnippet(snippet)
    }

    suspend fun updateSnippet(snippet: CommunitySnippet) {
        communityDao.updateSnippet(snippet)
    }

    suspend fun deleteSnippet(id: Long) {
        communityDao.deleteSnippet(id)
    }

    suspend fun toggleLike(snippet: CommunitySnippet) {
        val newIsLiked = !snippet.isLikedByMe
        val newCount = if (newIsLiked) snippet.likeCount + 1 else (snippet.likeCount - 1).coerceAtLeast(0)
        val updated = snippet.copy(isLikedByMe = newIsLiked, likeCount = newCount)
        communityDao.updateSnippet(updated)
    }

    suspend fun addComment(comment: CommunityComment): Long {
        return communityDao.insertComment(comment)
    }
}
