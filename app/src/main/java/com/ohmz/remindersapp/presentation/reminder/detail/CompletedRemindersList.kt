package com.ohmz.remindersapp.presentation.reminder.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                            ScheduledReminderItem(
                                reminder = reminder,
                                onCheckedChange = { onCheckedChange(reminder) },
                                onDeleteClick = { onDeleteClick(reminder) },
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
                            ScheduledReminderItem(
                                reminder = reminder,
                                onCheckedChange = { onCheckedChange(reminder) },
                                onDeleteClick = { onDeleteClick(reminder) },
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
                            ScheduledReminderItem(
                                reminder = reminder,
                                onCheckedChange = { onCheckedChange(reminder) },
                                onDeleteClick = { onDeleteClick(reminder) },
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
