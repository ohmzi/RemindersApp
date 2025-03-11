package com.ohmz.remindersapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ohmz.remindersapp.domain.model.Reminder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReminderItem(
    reminder: Reminder,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isCompleted)
                Color(0xFFF5F5F5) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Checkbox
            Checkbox(
                checked = reminder.isCompleted,
                onCheckedChange = { onToggleComplete() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Reminder content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (reminder.isCompleted)
                        TextDecoration.LineThrough else TextDecoration.None,
                    color = if (reminder.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                if (!reminder.notes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reminder.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (reminder.isCompleted)
                            TextDecoration.LineThrough else TextDecoration.None
                    )
                }

                if (reminder.dueDate != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatDate(reminder.dueDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isOverdue(reminder.dueDate) && !reminder.isCompleted)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun formatDate(date: Date): String {
    val format = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return format.format(date)
}

private fun isOverdue(date: Date): Boolean {
    return date.before(Date())
}