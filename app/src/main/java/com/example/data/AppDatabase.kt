package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CommunityDao
import com.example.data.dao.DiaryDao
import com.example.data.dao.FriendDao
import com.example.data.dao.UserSettingsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [DiaryEntry::class, Friend::class, CommunitySnippet::class, CommunityComment::class, UserSettings::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
    abstract fun friendDao(): FriendDao
    abstract fun communityDao(): CommunityDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "warm_journal_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        suspend fun populateInitialData(db: AppDatabase) {
            val userSettingsDao = db.userSettingsDao()

            // Initial User Settings only
            userSettingsDao.insertOrUpdateSettings(
                UserSettings(
                    id = 1,
                    isBiometricEnabled = false,
                    pinCode = "1234",
                    themeMode = "SYSTEM",
                    nickname = "暖暖的记录者",
                    bio = "慢下来，记录生活的微光与温柔。",
                    userId = "warm_me_666",
                    isAutoSyncEnabled = true
                )
            )
        }
    }
}
