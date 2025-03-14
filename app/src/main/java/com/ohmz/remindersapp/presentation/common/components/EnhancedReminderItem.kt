package com.ohmz.remindersapp.presentation.common.components

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
import java.text.SimpleDateFormat
import java.util.*

/**
 * Enhanced iOS-style reminder item
 */
@Composable
fun EnhancedReminderItem(
    reminder: Reminder,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    onFavoriteToggle: (Boolean) -> Unit = {},  // Add favorite toggle handler with default empty implementation
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // iOS-style circular checkbox
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable(onClick = { onCheckedChange(!reminder.isCompleted) })
                .background(
                    if (reminder.isCompleted) IOSColors.Blue
                    else IOSColors.White
                )
                .border(
                    width = 1.5.dp,
                    color = if (reminder.isCompleted) IOSColors.Blue else IOSColors.Gray3,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (reminder.isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = IOSColors.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content column with title, notes, date
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Priority indicator (color dot)
                val priorityColor = when (reminder.priority) {
                    Priority.LOW -> IOSColors.Green
                    Priority.MEDIUM -> IOSColors.Blue
                    Priority.HIGH -> IOSColors.Red
                }

                // Always show priority indicator regardless of priority level
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(priorityColor)
                )
                Spacer(modifier = Modifier.width(6.dp))

                // Title with styling
                Text(
                    text = reminder.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (reminder.isCompleted) IOSColors.Gray else IOSColors.Black,
                    textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
            }

            // Notes (if available)
            if (!reminder.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reminder.notes,
                    fontSize = 14.sp,
                    color = IOSColors.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Due date (if available)
            if (reminder.dueDate != null) {
                Spacer(modifier = Modifier.height(4.dp))

                val isOverdue = reminder.dueDate.before(Date()) && !reminder.isCompleted
                val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Due date",
                        tint = if (isOverdue) IOSColors.Red else IOSColors.Gray,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = dateFormat.format(reminder.dueDate),
                        fontSize = 13.sp,
                        color = if (isOverdue) IOSColors.Red else IOSColors.Gray
                    )
                }
            }
        }

        // Flag icon if favorited
        if (reminder.isFavorite) {
            IconButton(
                onClick = { onFavoriteToggle(!reminder.isFavorite) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite, 
                    contentDescription = "Favorited",
                    tint = IOSColors.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Delete button (iOS-style)
        IconButton(
            onClick = onDeleteClick
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = IOSColors.Red
            )
        }
    }

    // Light divider between items (iOS style)
    Divider(
        color = IOSColors.Gray5,
        thickness = 0.5.dp,
        modifier = Modifier.padding(start = 56.dp)
    )
}