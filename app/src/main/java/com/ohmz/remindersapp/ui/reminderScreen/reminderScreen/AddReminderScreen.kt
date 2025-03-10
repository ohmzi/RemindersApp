package com.ohmz.remindersapp.ui.reminderScreen.reminderScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ohmz.remindersapp.data.ReminderAction
import com.ohmz.remindersapp.ui.reminderScreen.viewModel.RemindersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderScreen(
    onSave: () -> Unit, onCancel: () -> Unit, viewModel: RemindersViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") } // For simplicity, just a string
    val isAddEnabled = title.isNotBlank()

    var selectedAction by remember { mutableStateOf<ReminderAction?>(null) }

    // Show keyboard immediately
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        keyboardController?.show()
    }

    // Use Scaffold without a topBar, then create a custom row at the top
    Scaffold(
        topBar = {}, // We’ll do a custom row instead
        bottomBar = {
            // The accessory bar at the bottom
            AccessoryBar(selectedAction = selectedAction, onActionSelected = { action ->
                selectedAction = if (selectedAction == action) null else action
            })
        },
        // Remove default insets/padding
        containerColor = Color.Transparent, contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        // Main content
        Column(
            modifier = Modifier.padding(padding).padding(top = 10.dp).fillMaxSize()
        ) {
            // iOS-like top row: flush at the top, minimal vertical padding
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: "Cancel"
                TextButton(onClick = onCancel) {
                    Text("Cancel", color = Color.Blue)
                }

                // Center: Title
                Text(
                    text = "New Reminder",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )

                // Right: "Add"
                TextButton(
                    onClick = {
                        // Convert dueDate string to a Long? if needed
                        val dueDateLong = dueDate.toLongOrNull()
                        viewModel.addReminder(title, notes, dueDateLong)
                        onSave()
                    }, enabled = isAddEnabled
                ) {
                    val textColor = if (isAddEnabled) Color.Blue else Color.Gray
                    Text("Add", fontWeight = FontWeight.ExtraBold ,color = textColor)
                }
            }


            TitleNotesCard(
                title = title,
                onTitleChange = { title = it },
                notes = notes,
                onNotesChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

