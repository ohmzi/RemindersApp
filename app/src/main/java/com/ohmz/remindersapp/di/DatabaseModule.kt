package com.ohmz.remindersapp.di

import android.content.Context
import androidx.room.Room
import com.ohmz.remindersapp.data.local.dao.ReminderDao
import com.ohmz.remindersapp.data.local.dao.ReminderListDao
import com.ohmz.remindersapp.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "reminders_database"
        )
            .fallbackToDestructiveMigration() // This will recreate the database if the version changes
            .build()
    }

    @Provides
    @Singleton
    fun provideReminderDao(database: AppDatabase): ReminderDao {
        return database.reminderDao()
    }
    
    @Provides
    @Singleton
    fun provideReminderListDao(database: AppDatabase): ReminderListDao {
        return database.reminderListDao()
    }
}
