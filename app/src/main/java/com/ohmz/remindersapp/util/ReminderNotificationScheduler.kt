package com.ohmz.remindersapp.util

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ohmz.remindersapp.domain.model.Reminder
import com.ohmz.remindersapp.worker.ReminderNotificationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Scheduler for reminder notifications
 */
@Singleton
class ReminderNotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager
) {
    
    /**
     * Schedules notifications for a reminder
     * - 12 hours before due date
     * - 3 hours before due date
     * - 15 minutes before due date
     */
    fun scheduleReminderNotifications(reminder: Reminder) {
        // Only schedule if the reminder has a due date
        val dueDate = reminder.dueDate ?: return
        
        // Calculate notification times
        val currentTime = Date()
        val twelveHoursBefore = calculateTimeBeforeDue(dueDate, TimeUnit.HOURS.toMillis(12))
        val threeHoursBefore = calculateTimeBeforeDue(dueDate, TimeUnit.HOURS.toMillis(3))
        val fifteenMinsBefore = calculateTimeBeforeDue(dueDate, TimeUnit.MINUTES.toMillis(15))
        
        // Only schedule notifications that are in the future
        if (twelveHoursBefore > currentTime) {
            scheduleNotification(
                reminder.id,
                twelveHoursBefore.time - currentTime.time,
                NotificationType.HOURS_12
            )
        }
        
        if (threeHoursBefore > currentTime) {
            scheduleNotification(
                reminder.id,
                threeHoursBefore.time - currentTime.time,
                NotificationType.HOURS_3
            )
        }
        
        if (fifteenMinsBefore > currentTime) {
            scheduleNotification(
                reminder.id,
                fifteenMinsBefore.time - currentTime.time,
                NotificationType.MINUTES_15
            )
        }
    }
    
    /**
     * Cancels all scheduled notifications for a reminder
     */
    fun cancelReminderNotifications(reminderId: Int) {
        // Cancel each type of notification
        NotificationType.values().forEach { type ->
            val workName = ReminderNotificationWorker.Builder.createWorkName(reminderId, type)
            workManager.cancelUniqueWork(workName)
        }
    }
    
    /**
     * Reschedules notifications for a reminder
     * Used when a reminder is updated
     */
    fun rescheduleReminderNotifications(reminder: Reminder) {
        // Cancel existing notifications first
        cancelReminderNotifications(reminder.id)
        
        // Schedule new notifications
        scheduleReminderNotifications(reminder)
    }
    
    /**
     * Schedules a single notification for a specific time before due date
     */
    private fun scheduleNotification(
        reminderId: Int,
        delayMillis: Long,
        notificationType: NotificationType
    ) {
        // Create work request with delay
        val notificationWork = OneTimeWorkRequestBuilder<ReminderNotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(ReminderNotificationWorker.Builder.createInputData(reminderId, notificationType))
            .build()
        
        // Enqueue unique work to ensure only one notification of each type per reminder
        val workName = ReminderNotificationWorker.Builder.createWorkName(reminderId, notificationType)
        workManager.enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE,
            notificationWork
        )
    }
    
    /**
     * Calculates a time X milliseconds before the due date
     */
    private fun calculateTimeBeforeDue(dueDate: Date, millisBeforeDue: Long): Date {
        return Date(dueDate.time - millisBeforeDue)
    }
}