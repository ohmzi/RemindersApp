package com.ohmz.remindersapp.ui.reminderScreen.reminderScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ohmz.remindersapp.data.ReminderAction

@Composable
fun AddReminderScreenWithAccessory() {
    // States for your fields
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Track which accessory action is selected
    var selectedAction by remember { mutableStateOf<ReminderAction?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top area for text fields, etc.
            TextField(value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = { Text("Title") })
            TextField(value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = { Text("Notes") })
        }

        // Accessory bar anchored at the bottom of the screen
        AccessoryBar(
            selectedAction = selectedAction, onActionSelected = { action ->
                // Toggle if the user taps the same icon again
                selectedAction = if (selectedAction == action) null else action
            }, modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
