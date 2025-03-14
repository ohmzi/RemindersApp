package com.ohmz.remindersapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ohmz.remindersapp.data.local.dao.ReminderDao
import com.ohmz.remindersapp.data.local.dao.ReminderListDao
import com.ohmz.remindersapp.data.local.entity.ReminderEntity
import com.ohmz.remindersapp.data.local.entity.ReminderListEntity

@Database(
    entities = [ReminderEntity::class, ReminderListEntity::class],
    version = 3, // Increment version because we're changing schema
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
    abstract fun reminderListDao(): ReminderListDao
}
