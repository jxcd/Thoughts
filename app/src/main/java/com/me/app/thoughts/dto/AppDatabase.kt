package com.me.app.thoughts.dto

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Thought::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun thoughtDao(): ThoughtDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ThoughtDatabase"
                )
                    // .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }

    }
}

lateinit var thoughtDao: () -> ThoughtDao