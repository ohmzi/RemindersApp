package com.ohmz.remindersapp.ui.reminderScreen.reminderScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ohmz.remindersapp.data.ReminderEntity
import com.ohmz.remindersapp.ui.reminderScreen.viewModel.RemindersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListScreen(
    onAddReminderClick: () -> Unit, viewModel: RemindersViewModel = hiltViewModel()
) {
    val reminders = viewModel.reminders.collectAsState().value

    Scaffold(topBar = {
        TopAppBar(title = { Text("Reminders") })
    }, floatingActionButton = {
        FloatingActionButton(onClick = onAddReminderClick) {
            Text("+")
        }
    }) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            items(reminders) { reminder ->
                ReminderItem(reminder = reminder,
                    onToggleComplete = { viewModel.toggleCompletion(reminder) },
                    onDelete = { viewModel.deleteReminder(reminder) })
            }
        }
    }
}

@Composable
fun ReminderItem(
    reminder: ReminderEntity, onToggleComplete: () -> Unit, onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = reminder.title, style = MaterialTheme.typography.bodyMedium)
                if (!reminder.description.isNullOrBlank()) {
                    Text(text = reminder.description)
                }
                if (reminder.dueDate != null) {
                    Text(text = "Due: ${reminder.dueDate}") // format date in real apps
                }
            }
            Column {
                Checkbox(checked = reminder.isCompleted, onCheckedChange = { onToggleComplete() })
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}
