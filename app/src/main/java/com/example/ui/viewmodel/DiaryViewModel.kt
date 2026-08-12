package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CommunitySnippet
import com.example.data.DiaryEntry
import com.example.data.repository.CommunityRepository
import com.example.data.repository.DiaryRepository
import com.example.data.repository.UserSettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val diaryRepo = DiaryRepository(db.diaryDao())
    private val communityRepo = CommunityRepository(db.communityDao())
    private val userSettingsRepo = UserSettingsRepository(db.userSettingsDao())

    val recordedDates: StateFlow<List<String>> = diaryRepo.recordedDates.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMood = MutableStateFlow<String?>(null)
    val selectedMood: StateFlow<String?> = _selectedMood.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredEntries: StateFlow<List<DiaryEntry>> = combine(
        _selectedDate,
        _searchQuery,
        _selectedMood
    ) { date, query, mood ->
        Triple(date, query, mood)
    }.flatMapLatest { (date, query, mood) ->
        val baseFlow = when {
            query.isNotBlank() -> diaryRepo.searchEntries(query)
            date != null -> diaryRepo.getEntriesByDate(date)
            else -> diaryRepo.allEntries
        }
        baseFlow
    }.combine(_selectedMood) { entries, moodFilter ->
        if (moodFilter == null) {
            entries
        } else {
            entries.filter { it.mood.contains(moodFilter) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectDate(date: String?) {
        _selectedDate.value = date
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectMoodFilter(mood: String?) {
        _selectedMood.value = mood
    }

    fun saveEntry(
        id: Long = 0,
        title: String,
        content: String,
        date: String,
        formattedDate: String,
        mood: String,
        weather: String,
        imageUris: String,
        tags: String,
        location: String,
        shareToCommunity: Boolean,
        privacyLevel: String
    ) {
        viewModelScope.launch {
            val entry = DiaryEntry(
                id = id,
                title = title.ifBlank { "无题日记" },
                content = content,
                date = date,
                formattedDate = formattedDate,
                mood = mood,
                weather = weather,
                imageUris = imageUris,
                tags = tags,
                location = location,
                isSharedToCommunity = shareToCommunity,
                privacyLevel = privacyLevel
            )
            val savedId = if (id == 0L) {
                diaryRepo.insert(entry)
            } else {
                diaryRepo.update(entry)
                id
            }

            if (shareToCommunity) {
                val user = userSettingsRepo.getSettingsDirect()
                communityRepo.publishSnippet(
                    CommunitySnippet(
                        authorId = user.userId,
                        authorName = user.nickname,
                        diaryId = savedId,
                        title = entry.title,
                        excerpt = if (content.length > 80) content.take(80) + "..." else content,
                        imageUrl = imageUris.split(",").firstOrNull { it.isNotBlank() } ?: "",
                        mood = mood,
                        publishDate = date
                    )
                )
            }
        }
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            diaryRepo.deleteById(id)
        }
    }

    fun formatDateToDisplay(dateMillis: Long): Pair<String, String> {
        val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("yyyy年M月d日 EEEE", Locale.CHINA)
        val dateObj = Date(dateMillis)
        return Pair(isoFormat.format(dateObj), displayFormat.format(dateObj))
    }
}
