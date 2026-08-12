package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.DiaryEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary_entries ORDER BY date DESC, timestamp DESC")
    fun getAllEntries(): Flow<List<DiaryEntry>>

    @Query("SELECT * FROM diary_entries ORDER BY date DESC, timestamp DESC")
    suspend fun getAllEntriesList(): List<DiaryEntry>

    @Query("SELECT * FROM diary_entries WHERE date = :date ORDER BY timestamp DESC")
    fun getEntriesByDate(date: String): Flow<List<DiaryEntry>>

    @Query("SELECT * FROM diary_entries WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchEntries(query: String): Flow<List<DiaryEntry>>

    @Query("SELECT * FROM diary_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): DiaryEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DiaryEntry): Long

    @Update
    suspend fun updateEntry(entry: DiaryEntry)

    @Query("DELETE FROM diary_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Long)

    @Query("SELECT COUNT(*) FROM diary_entries")
    fun getEntryCount(): Flow<Int>

    @Query("SELECT DISTINCT date FROM diary_entries")
    fun getAllRecordedDates(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<DiaryEntry>)
}
