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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    onFavoriteToggle: (Reminder, Boolean) -> Unit
) {
    // The reminders should already be filtered for completed items by the viewModel
    // Sort completed reminders by completion date (most recent first) - using dueDate as proxy
    val completedReminders = reminders.sortedByDescending { it.dueDate }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
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
            // Section header for completed items
            item {
            }
            
            // Group by completion period (Today, This Week, Earlier)
            val today = Calendar.getInstance()
            val lastWeek = Calendar.getInstance().apply { 
                add(Calendar.DAY_OF_YEAR, -7) 
            }
            
            val todayCompleted = completedReminders.filter { reminder ->
                reminder.dueDate?.let { date ->
                    val cal = Calendar.getInstance().apply { time = date }
                    DateUtils.isSameDay(cal, today)
                } ?: false
            }
            
            val thisWeekCompleted = completedReminders.filter { reminder ->
                reminder.dueDate?.let { date ->
                    val cal = Calendar.getInstance().apply { time = date }
                    !DateUtils.isSameDay(cal, today) && cal.after(lastWeek)
                } ?: false
            }
            
            val earlierCompleted = completedReminders.filter { reminder ->
                reminder.dueDate?.let { date ->
                    val cal = Calendar.getInstance().apply { time = date }
                    cal.before(lastWeek)
                } ?: true // Include null dates in "earlier"
            }
            
            // Today section
            if (todayCompleted.isNotEmpty()) {
                item {
                    Text(
                        text = "Today",
                        fontWeight = FontWeight.Medium,
                        fontSize = 17.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
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
            
            // This Week section
            if (thisWeekCompleted.isNotEmpty()) {
                item {
                    Text(
                        text = "This Week",
                        fontWeight = FontWeight.Medium,
                        fontSize = 17.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                    )
                }
                
                thisWeekCompleted.forEach { reminder ->
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
            
            // Earlier section
            if (earlierCompleted.isNotEmpty()) {

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
                // Priority color dot
                if (reminder.priority != Priority.MEDIUM) {
                    val dotColor = when(reminder.priority) {
                        Priority.LOW -> Color(0xFF34C759)
                        Priority.HIGH -> Color(0xFFFF3B30)
                        else -> Color.Transparent
                    }

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )

                    Spacer(modifier = Modifier.width(6.dp))
                }

                // Blue dot for list color (iOS style)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF007AFF))
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
}
