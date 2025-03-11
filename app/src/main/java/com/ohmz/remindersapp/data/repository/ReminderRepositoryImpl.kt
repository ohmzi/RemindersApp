package com.ohmz.remindersapp.data.repository

import com.ohmz.remindersapp.data.local.dao.ReminderDao
import com.ohmz.remindersapp.data.mapper.toDomainModel
import com.ohmz.remindersapp.data.mapper.toEntity
import com.ohmz.remindersapp.domain.model.Reminder
import com.ohmz.remindersapp.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao
) : ReminderRepository {

    override fun getReminders(): Flow<List<Reminder>> {
        return reminderDao.getAllReminders().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getReminderById(id: Int): Reminder? {
        return reminderDao.getReminderById(id)?.toDomainModel()
    }

    override suspend fun addReminder(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder.toEntity())
    }

    override suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder.toEntity())
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder.toEntity())
    }

    override fun getRemindersByCompletionStatus(isCompleted: Boolean): Flow<List<Reminder>> {
        return reminderDao.getRemindersByCompletionStatus(isCompleted).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getFavoriteReminders(): Flow<List<Reminder>> {
        return reminderDao.getFavoriteReminders().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getScheduledReminders(): Flow<List<Reminder>> {
        return reminderDao.getScheduledReminders().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getTodayReminders(): Flow<List<Reminder>> {
        val calendar = Calendar.getInstance()

        // Start of today
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        // End of today
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        return reminderDao.getRemindersForDay(startOfDay, endOfDay).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}