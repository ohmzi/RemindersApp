package com.ohmz.remindersapp.data.local.dao

import androidx.room.*
import com.ohmz.remindersapp.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY id DESC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Int): ReminderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders WHERE isCompleted = :isCompleted ORDER BY id DESC")
    fun getRemindersByCompletionStatus(isCompleted: Boolean): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isFavorite = 1 ORDER BY id DESC")
    fun getFavoriteReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE dueDate IS NOT NULL ORDER BY dueDate ASC")
    fun getScheduledReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE dueDate >= :startOfDay AND dueDate < :endOfDay ORDER BY dueDate ASC")
    fun getRemindersForDay(startOfDay: Long, endOfDay: Long): Flow<List<ReminderEntity>>
}
