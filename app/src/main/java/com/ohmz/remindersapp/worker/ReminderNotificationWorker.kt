package com.ohmz.remindersapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.ohmz.remindersapp.domain.repository.ReminderRepository
import com.ohmz.remindersapp.util.NotificationHelper
import com.ohmz.remindersapp.util.NotificationType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * WorkManager worker that handles sending notifications for reminders
 */
class ReminderNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val reminderRepository: ReminderRepository
) : CoroutineWorker(context, params) {

    /**
     * Factory for creating instances of ReminderNotificationWorker
     * This allows Hilt to inject dependencies into the worker
     */
    @AssistedFactory
    interface Factory {
        fun create(context: Context, params: WorkerParameters): ReminderNotificationWorker
    }

    companion object {
        const val KEY_REMINDER_ID = "reminder_id"
        const val KEY_NOTIFICATION_TYPE = "notification_type"
        
        // Keys for creating notification work data
        const val WORK_NAME_PREFIX = "reminder_notification_"
    }
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Get input data
            val reminderId = inputData.getInt(KEY_REMINDER_ID, -1)
            val notificationTypeValue = inputData.getString(KEY_NOTIFICATION_TYPE)
            
            if (reminderId == -1 || notificationTypeValue == null) {
                return@withContext Result.failure()
            }
            
            // Convert string to NotificationType
            val notificationType = NotificationType.valueOf(notificationTypeValue)
            
            // Get the reminder
            val reminder = reminderRepository.getReminderById(reminderId) ?: return@withContext Result.failure()
            
            // Verify reminder still has a due date and is not completed
            if (reminder.dueDate == null || reminder.isCompleted) {
                return@withContext Result.success()
            }
            
            // Create notification helper
            val notificationHelper = NotificationHelper(context)
            
            // Calculate timeframe text
            val timeframeText = when (notificationType) {
                NotificationType.HOURS_12 -> "in 12 hours"
                NotificationType.HOURS_3 -> "in 3 hours"
                NotificationType.MINUTES_15 -> "in 15 minutes"
            }
            
            // Show notification
            notificationHelper.showReminderNotification(
                reminderId = reminder.id,
                title = reminder.title,
                content = reminder.notes,
                timeframe = timeframeText,
                notificationType = notificationType
            )
            
            return@withContext Result.success()
        } catch (e: Exception) {
            return@withContext Result.failure()
        }
    }
    
    /**
     * Create input data for the worker
     */
    class Builder {
        companion object {
            fun createInputData(reminderId: Int, notificationType: NotificationType): Data {
                return Data.Builder()
                    .putInt(KEY_REMINDER_ID, reminderId)
                    .putString(KEY_NOTIFICATION_TYPE, notificationType.name)
                    .build()
            }
            
            fun createWorkName(reminderId: Int, notificationType: NotificationType): String {
                return "${WORK_NAME_PREFIX}${reminderId}_${notificationType.name}"
            }
        }
    }
}