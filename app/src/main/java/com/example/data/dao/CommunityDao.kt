package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.CommunityComment
import com.example.data.CommunitySnippet
import kotlinx.coroutines.flow.Flow

@Dao
interface CommunityDao {
    @Query("SELECT * FROM community_snippets ORDER BY timestamp DESC")
    fun getAllSnippets(): Flow<List<CommunitySnippet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnippet(snippet: CommunitySnippet): Long

    @Update
    suspend fun updateSnippet(snippet: CommunitySnippet)

    @Query("DELETE FROM community_snippets WHERE id = :id")
    suspend fun deleteSnippet(id: Long)

    @Query("SELECT * FROM community_comments WHERE snippetId = :snippetId ORDER BY timestamp ASC")
    fun getCommentsForSnippet(snippetId: Long): Flow<List<CommunityComment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommunityComment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSnippets(snippets: List<CommunitySnippet>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllComments(comments: List<CommunityComment>)
}
