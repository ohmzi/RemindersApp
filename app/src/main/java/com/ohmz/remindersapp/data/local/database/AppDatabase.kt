package com.ohmz.remindersapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ohmz.remindersapp.data.local.dao.ReminderDao
import com.ohmz.remindersapp.data.local.entity.ReminderEntity

@Database(entities = [ReminderEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
}
