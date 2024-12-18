package com.example.mood.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mood.model.MoodHistory
import com.example.mood.model.MoodType
import com.example.mood.model.User
import com.example.mood.model.UserMood
import com.example.mood.objects.MoodTypeDao
import com.example.mood.objects.UserDao
import com.example.mood.objects.UserMoodDao
import com.example.mood.objects.UserMoodHistoryDao

object DatabaseProvider {
    @Database(entities = [User::class, MoodType::class, MoodHistory::class, UserMood::class], version = 7, exportSchema = false)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun userDao(): UserDao
        abstract fun moodTypeDao(): MoodTypeDao
        abstract fun userMoodDao(): UserMoodDao
        abstract fun userMoodHistoryDao(): UserMoodHistoryDao

        companion object {
            @Volatile
            private var INSTANCE: AppDatabase? = null

            fun getDatabase(context: Context): AppDatabase {
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "mood_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                    instance
                }

            }
        }
    }
}