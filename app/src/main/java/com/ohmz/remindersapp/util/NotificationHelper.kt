package com.ohmz.remindersapp.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ohmz.remindersapp.MainActivity
import com.ohmz.remindersapp.R

/**
 * Helper class to manage notifications in the app
 */
class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID_REMINDERS = "reminders_channel"
        const val NOTIFICATION_GROUP = "com.ohmz.remindersapp.REMINDERS"
        
        // Notification IDs - using offset to avoid collision
        const val NOTIFICATION_ID_OFFSET_12_HOURS = 100000
        const val NOTIFICATION_ID_OFFSET_3_HOURS = 200000
        const val NOTIFICATION_ID_OFFSET_15_MINS = 300000
    }

    init {
        createNotificationChannel()
    }

    /**
     * Creates the notification channels for Android O and above
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminders"
            val descriptionText = "Notifications for upcoming reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_REMINDERS, name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Shows a notification for an upcoming reminder
     * 
     * @param reminderId ID of the reminder
     * @param title Title of the reminder
     * @param content Content/description of the reminder
     * @param timeframe Text indicating how soon the reminder is due (e.g., "in 12 hours")
     * @param notificationType Type of notification (12 hours, 3 hours, or 15 minutes)
     */
    fun showReminderNotification(
        reminderId: Int,
        title: String,
        content: String?,
        timeframe: String,
        notificationType: NotificationType
    ) {
        // Create intent for tapping on the notification
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("REMINDER_ID", reminderId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 
            reminderId, 
            intent, 
            PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use appropriate icon
            .setContentTitle(title)
            .setContentText("Due $timeframe")
            .setStyle(NotificationCompat.BigTextStyle().bigText(content ?: "Due $timeframe"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setGroup(NOTIFICATION_GROUP)

        // Determine notification ID based on type
        val notificationId = when (notificationType) {
            NotificationType.HOURS_12 -> reminderId + NOTIFICATION_ID_OFFSET_12_HOURS
            NotificationType.HOURS_3 -> reminderId + NOTIFICATION_ID_OFFSET_3_HOURS
            NotificationType.MINUTES_15 -> reminderId + NOTIFICATION_ID_OFFSET_15_MINS
        }

        // Show the notification if permission is granted
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build())
        }
    }

    /**
     * Cancels a notification
     */
    fun cancelNotification(reminderId: Int, notificationType: NotificationType) {
        val notificationId = when (notificationType) {
            NotificationType.HOURS_12 -> reminderId + NOTIFICATION_ID_OFFSET_12_HOURS
            NotificationType.HOURS_3 -> reminderId + NOTIFICATION_ID_OFFSET_3_HOURS
            NotificationType.MINUTES_15 -> reminderId + NOTIFICATION_ID_OFFSET_15_MINS
        }
        
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
}

/**
 * Enum representing the different notification timeframes
 */
enum class NotificationType {
    HOURS_12,
    HOURS_3,
    MINUTES_15
}