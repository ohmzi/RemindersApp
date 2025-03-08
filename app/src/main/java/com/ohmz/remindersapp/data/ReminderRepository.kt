package com.ohmz.remindersapp.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(
    private val dao: ReminderDao
) {
    fun getAllReminders(): Flow<List<ReminderEntity>> = dao.getAllReminders()

    suspend fun insertReminder(reminder: ReminderEntity) {
        dao.insertReminder(reminder)
    }

    suspend fun updateReminder(reminder: ReminderEntity) {
        dao.updateReminder(reminder)
    }

    suspend fun deleteReminder(reminder: ReminderEntity) {
        dao.deleteReminder(reminder)
    }
}
