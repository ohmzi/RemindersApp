package com.ohmz.remindersapp.presentation.reminder.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ohmz.remindersapp.domain.model.Priority
import com.ohmz.remindersapp.domain.model.Reminder
import com.ohmz.remindersapp.presentation.common.theme.IOSColors

/**
 * A special version of reminder item for the Scheduled screen that doesn't show dates
 * (since they're already shown in the section headers)
 */
@Composable
fun ScheduledReminderItem(
    reminder: Reminder,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
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
                    com.ohmz.remindersapp.domain.model.Priority.LOW -> Color(0xFF34C759)
                    com.ohmz.remindersapp.domain.model.Priority.MEDIUM -> Color(0xFF007AFF)
                    com.ohmz.remindersapp.domain.model.Priority.HIGH -> Color(0xFFFF3B30)
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

            // List name removed as requested
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

        // Delete icon
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFFF3B30)
            )
        }
    }
}