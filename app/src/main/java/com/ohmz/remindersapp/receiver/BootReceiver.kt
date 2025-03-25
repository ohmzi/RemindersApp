package com.ohmz.remindersapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ohmz.remindersapp.util.ReminderNotificationScheduler
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Broadcast receiver that reschedules notifications when the device boots
 */
class BootReceiver : BroadcastReceiver() {
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val hiltEntryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                BootReceiverEntryPoint::class.java
            )
            
            val reminderRepository = hiltEntryPoint.reminderRepository()
            val workManager = hiltEntryPoint.workManager()
            val notificationScheduler = ReminderNotificationScheduler(context, workManager)
            
            scope.launch {
                // Get incomplete reminders with due dates
                val reminders = reminderRepository.getReminders().first()
                    .filter { !it.isCompleted && it.dueDate != null }
                
                // Reschedule notifications for each reminder
                reminders.forEach { reminder ->
                    notificationScheduler.scheduleReminderNotifications(reminder)
                }
            }
        }
    }
}