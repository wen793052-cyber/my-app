package com.example.data.repository

import com.example.data.DiaryEntry
import com.example.data.dao.DiaryDao
import kotlinx.coroutines.flow.Flow

class DiaryRepository(private val diaryDao: DiaryDao) {
    val allEntries: Flow<List<DiaryEntry>> = diaryDao.getAllEntries()
    val entryCount: Flow<Int> = diaryDao.getEntryCount()
    val recordedDates: Flow<List<String>> = diaryDao.getAllRecordedDates()

    fun getEntriesByDate(date: String): Flow<List<DiaryEntry>> {
        return diaryDao.getEntriesByDate(date)
    }

    fun searchEntries(query: String): Flow<List<DiaryEntry>> {
        return diaryDao.searchEntries(query)
    }

    suspend fun getEntryById(id: Long): DiaryEntry? {
        return diaryDao.getEntryById(id)
    }

    suspend fun getAllEntriesList(): List<DiaryEntry> {
        return diaryDao.getAllEntriesList()
    }

    suspend fun insertAll(entries: List<DiaryEntry>) {
        diaryDao.insertAll(entries)
    }

    suspend fun insert(entry: DiaryEntry): Long {
        return diaryDao.insertEntry(entry)
    }

    suspend fun update(entry: DiaryEntry) {
        diaryDao.updateEntry(entry)
    }

    suspend fun deleteById(id: Long) {
        diaryDao.deleteEntryById(id)
    }
}
