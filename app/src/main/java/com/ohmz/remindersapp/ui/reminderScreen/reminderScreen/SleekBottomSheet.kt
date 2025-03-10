package com.ohmz.remindersapp.ui.reminderScreen.reminderScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleekBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isAddEnabled: Boolean
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = {},
        shape = RectangleShape
    ) {
        // TOP BAR (flush at the top)
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: "Cancel"
            TextButton(onClick = onCancel) {
                Text("Cancel", color = Color.Blue)
            }

            // Center: Title
            Text(
                text = "New Reminder", style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            // Right: "Add"
            TextButton(
                onClick = onSave, enabled = isAddEnabled
            ) {
                val textColor = if (isAddEnabled) Color.Blue else Color.Gray
                Text("Add", color = textColor)
            }
        }
    }
}
