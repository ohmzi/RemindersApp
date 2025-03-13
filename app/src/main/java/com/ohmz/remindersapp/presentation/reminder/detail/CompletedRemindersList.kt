package com.ohmz.remindersapp.presentation.reminder.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ohmz.remindersapp.domain.model.Priority
import com.ohmz.remindersapp.domain.model.Reminder
import com.ohmz.remindersapp.presentation.common.utils.DateUtils
import java.util.Calendar

@Composable
fun CompletedRemindersList(
    reminders: List<Reminder>,
    onCheckedChange: (Reminder) -> Unit,
    onDeleteClick: (Reminder) -> Unit,
    onFavoriteToggle: (Reminder, Boolean) -> Unit,
    onClearAllCompleted: () -> Unit = {} // New callback for clearing all completed reminders
) {
    // The reminders should already be filtered for completed items by the viewModel
    // Sort completed reminders by completion date (most recent first) - using dueDate as proxy
    val completedReminders = reminders.sortedByDescending { it.dueDate }

    // Show confirmation dialog state
    var showClearConfirmation by remember { mutableStateOf(false) }

    // Show the confirmation dialog if requested
    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = { Text("Clear Completed Reminders") },
            text = { Text("Are you sure you want to clear all completed reminders?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAllCompleted()
                        showClearConfirmation = false
                    }
                ) {
                    Text("Clear All Completed", color = Color(0xFFFF3B30)) // iOS red color
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // Section with count and clear button - styled to match iOS screenshot
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Count text (e.g., "1 Completed") with dot separator
                Text(
                    text = "${completedReminders.size} Completed • ",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal
                )

                // Clear button - no TextButton, just plain text to match iOS style
                Text(
                    text = "Clear",
                    fontSize = 16.sp,
                    color = Color(0xFF007AFF), // iOS blue
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.clickable { showClearConfirmation = true }
                )
            }
        }

        if (completedReminders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, bottom = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No completed tasks",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            // The app doesn't track when items were completed, only if they are completed
            // Since we need to display recently completed items under "Today"
            // and older completed items under "Earlier", we'll use a different approach
            
            // Assume that the most recently completed items are at the beginning
            // of the sorted list (which is sorted by dueDate descending)
            
            // Put the first few items (or all if there are few) in "Today"
            val recentlyCompletedCount = Math.min(5, completedReminders.size)
            
            val todayCompleted = if (recentlyCompletedCount > 0) {
                completedReminders.subList(0, recentlyCompletedCount)
            } else {
                emptyList()
            }
            
            // Put the rest in "Earlier"
            val earlierCompleted = if (completedReminders.size > recentlyCompletedCount) {
                completedReminders.subList(recentlyCompletedCount, completedReminders.size)
            } else {
                emptyList()
            }

            // Today section
            if (todayCompleted.isNotEmpty()) {
                item {
                    Text(
                        text = "Today",
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                todayCompleted.forEach { reminder ->
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                        ) {
                            CompletedReminderItem(
                                reminder = reminder,
                                onCheckedChange = { onCheckedChange(reminder) },
                                onFavoriteToggle = { isFavorite ->
                                    onFavoriteToggle(reminder, isFavorite)
                                }
                            )
                        }
                    }
                }

                item {
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE5E5EA))
                }
            }

            // We've removed the "This Week" section since we only want Today and Earlier

            // Earlier section
            if (earlierCompleted.isNotEmpty()) {
                item {
                    Text(
                        text = "Earlier",
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                earlierCompleted.forEach { reminder ->
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                        ) {
                            CompletedReminderItem(
                                reminder = reminder,
                                onCheckedChange = { onCheckedChange(reminder) },
                                onFavoriteToggle = { isFavorite ->
                                    onFavoriteToggle(reminder, isFavorite)
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * A modified version of ScheduledReminderItem specifically for completed reminders
 * that doesn't include a delete button
 */
@Composable
fun CompletedReminderItem(
    reminder: Reminder,
    onCheckedChange: (Boolean) -> Unit,
    onFavoriteToggle: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable(onClick = { onCheckedChange(!reminder.isCompleted) })
                .background(if (reminder.isCompleted) Color(0xFF007AFF) else Color.White)
                .border(
                    width = 1.5.dp,
                    color = if (reminder.isCompleted) Color(0xFF007AFF) else Color(0xFFD1D1D6),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (reminder.isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Title and notes
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Show a colored dot for priority if needed
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Priority color dot - only one dot for the priority
                val dotColor = when(reminder.priority) {
                    Priority.LOW -> Color(0xFF34C759)
                    Priority.MEDIUM -> Color(0xFF007AFF)
                    Priority.HIGH -> Color(0xFFFF3B30)
                }

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Title
                Text(
                    text = reminder.title,
                    fontWeight = FontWeight.Normal,
                    fontSize = 17.sp,
                    color = if (reminder.isCompleted) Color.Gray else Color.Black
                )
            }

            // Notes
            if (!reminder.notes.isNullOrBlank()) {
                Text(
                    text = reminder.notes,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            // Due date (we don't have completion date)
            reminder.dueDate?.let { date ->
                Text(
                    text = "Due: ${DateUtils.formatDateWithTime(date)}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        // Favorite icon
        if (reminder.isFavorite) {
            IconButton(
                onClick = { onFavoriteToggle(false) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = Color(0xFFFF3B30),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // No Delete icon for completed reminders
    }

    // Add a divider after each reminder
    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp),
        thickness = 0.5.dp,
        color = Color(0xFFE5E5EA)
    )
}