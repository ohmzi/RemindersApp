package com.ohmz.remindersapp.presentation.reminder.main

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohmz.remindersapp.MainActivity
import com.ohmz.remindersapp.R
import com.ohmz.remindersapp.domain.model.ReminderList
import com.ohmz.remindersapp.domain.repository.ReminderListRepository
import com.ohmz.remindersapp.domain.repository.ReminderRepository
import com.ohmz.remindersapp.presentation.common.theme.IOSColors
import com.ohmz.remindersapp.util.NotificationHelper
import com.ohmz.remindersapp.util.NotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReminderMainUiState(
    val lists: List<ReminderList> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val testNotificationStatus: String? = null,
    val isTestNotificationsEnabled: Boolean = false
)

@HiltViewModel
class ReminderMainViewModel @Inject constructor(
    private val reminderListRepository: ReminderListRepository,
    private val reminderRepository: ReminderRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReminderMainUiState(isLoading = true))
    val uiState: StateFlow<ReminderMainUiState> = _uiState.asStateFlow()
    
    private val notificationHelper = NotificationHelper(context)

    init {
        loadLists()
        loadSettings()
    }
    
    private fun loadSettings() {
        // Check if test notification channel is enabled via Android notification settings
        viewModelScope.launch {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Periodically check for notification channel status
            while (true) {
                // Get status of the test notification channel
                val isEnabled = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    val channel = notificationManager.getNotificationChannel(NotificationHelper.CHANNEL_ID_TEST)
                    channel?.importance != NotificationManager.IMPORTANCE_NONE
                } else {
                    // Pre-O devices just return true (channel concept doesn't exist)
                    true
                }
                
                _uiState.update { currentState ->
                    currentState.copy(isTestNotificationsEnabled = isEnabled)
                }
                
                // Check every 2 seconds - in a real app you might want to use a better strategy
                kotlinx.coroutines.delay(2000)
            }
        }
    }
    
    /**
     * Tests notifications by showing all three notification types
     * This is for debugging purposes only
     */
    fun testNotifications() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(testNotificationStatus = "Sending test notifications...") }
                
                // Create a test reminder that's due in 15 minutes
                val testReminderId = 9999  // Use a special ID for test
                
                // Create test notifications using a custom method
                showTestNotification(
                    testReminderId = testReminderId,
                    title = "Test 12-Hour Notification",
                    content = "This is a test notification that would normally appear 12 hours before the deadline",
                    timeframe = "in 12 hours",
                    notificationType = NotificationType.HOURS_12
                )
                
                // Wait a moment before sending the next notification
                kotlinx.coroutines.delay(1000)
                
                // Send 3-hour notification
                showTestNotification(
                    testReminderId = testReminderId + 1,
                    title = "Test 3-Hour Notification",
                    content = "This is a test notification that would normally appear 3 hours before the deadline",
                    timeframe = "in 3 hours",
                    notificationType = NotificationType.HOURS_3
                )
                
                // Wait a moment before sending the next notification
                kotlinx.coroutines.delay(1000)
                
                // Send 15-minute notification
                showTestNotification(
                    testReminderId = testReminderId + 2,
                    title = "Test 15-Minute Notification",
                    content = "This is a test notification that would normally appear 15 minutes before the deadline",
                    timeframe = "in 15 minutes",
                    notificationType = NotificationType.MINUTES_15
                )
                
                _uiState.update { it.copy(testNotificationStatus = "Test notifications sent!") }
                
                // Clear the status after 3 seconds
                kotlinx.coroutines.delay(3000)
                _uiState.update { it.copy(testNotificationStatus = null) }
                
            } catch (e: Exception) {
                _uiState.update { it.copy(testNotificationStatus = "Error: ${e.message}") }
                
                // Clear the error after 3 seconds
                kotlinx.coroutines.delay(3000)
                _uiState.update { it.copy(testNotificationStatus = null) }
            }
        }
    }
    
    /**
     * Show a test notification using the test notification channel
     */
    private fun showTestNotification(
        testReminderId: Int,
        title: String,
        content: String,
        timeframe: String,
        notificationType: NotificationType
    ) {
        // Create intent for tapping on the notification
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 
            testReminderId, 
            intent, 
            PendingIntent.FLAG_IMMUTABLE
        )

        // Determine notification ID based on type
        val notificationId = when (notificationType) {
            NotificationType.HOURS_12 -> testReminderId + NotificationHelper.NOTIFICATION_ID_OFFSET_12_HOURS
            NotificationType.HOURS_3 -> testReminderId + NotificationHelper.NOTIFICATION_ID_OFFSET_3_HOURS
            NotificationType.MINUTES_15 -> testReminderId + NotificationHelper.NOTIFICATION_ID_OFFSET_15_MINS
        }
        
        // Build the notification with the test channel
        val notificationBuilder = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID_TEST)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText("Due $timeframe")
            .setStyle(NotificationCompat.BigTextStyle().bigText(content ?: "Due $timeframe"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setGroup(NotificationHelper.NOTIFICATION_GROUP)

        // Show the notification if permission is granted
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build())
        }
    }

    private fun loadLists() {
        viewModelScope.launch {
            try {
                reminderListRepository.getAllLists().collect { lists ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            lists = lists,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Failed to load lists: ${e.message}"
                    )
                }
            }
        }
    }

    fun addList(name: String, color: Color = IOSColors.Blue) {
        viewModelScope.launch {
            try {
                val newList = ReminderList(name = name, color = color)
                reminderListRepository.addList(newList)
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = "Failed to add list: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Rename an existing list
     */
    fun renameList(list: ReminderList, newName: String) {
        if (newName.isBlank() || newName == list.name) return

        viewModelScope.launch {
            try {
                val updatedList = list.copy(name = newName)
                reminderListRepository.updateList(updatedList)
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = "Failed to rename list: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Update the color of an existing list
     */
    fun updateListColor(list: ReminderList, newColor: Color) {
        if (newColor == list.color) return

        viewModelScope.launch {
            try {
                val updatedList = list.copy(color = newColor)
                reminderListRepository.updateList(updatedList)
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = "Failed to update list color: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Delete an existing list
     */
    fun deleteList(list: ReminderList) {
        viewModelScope.launch {
            try {
                reminderListRepository.deleteList(list)
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = "Failed to delete list: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { currentState ->
            currentState.copy(error = null)
        }
    }
}