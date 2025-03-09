package com.ohmz.remindersapp.ui.reminderScreen.reminderScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
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
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") } // For simplicity, just a string

    var selectedAction by remember { mutableStateOf<ReminderAction?>(null) }

    // Create a FocusRequester for the title field
    val titleFocusRequester = remember { FocusRequester() }
    // Get the keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current

    // Request focus when the composable appears
    LaunchedEffect(Unit) {
        titleFocusRequester.requestFocus()
        keyboardController?.show()
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Add Reminder") })
    }, bottomBar = {
        AccessoryBar(selectedAction = selectedAction, onActionSelected = { action ->
            // Toggle if the user taps the same icon again
            selectedAction = if (selectedAction == action) null else action
        })
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(titleFocusRequester),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Button(onClick = {
                    // Convert dueDate string to a Long? if needed
                    val dueDateLong = dueDate.toLongOrNull()
                    viewModel.addReminder(title, description, dueDateLong)
                    onSave()
                }) {
                    Text("Save")
                }

                Spacer(modifier = Modifier.width(16.dp))

                OutlinedButton(onClick = onCancel) {
                    Text("Cancel")
                }
            }

        }
        // Accessory bar anchored at the bottom of the screen
    }
}
