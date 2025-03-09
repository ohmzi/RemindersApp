package com.ohmz.remindersapp.ui.reminderScreen.reminderScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ohmz.remindersapp.data.ReminderAction

@Composable
fun AccessoryBar(
    selectedAction: ReminderAction?,
    onActionSelected: (ReminderAction) -> Unit,
    modifier: Modifier = Modifier
) {
    // A column that can contain both the "sub-row" and the main row
    Column(
        modifier = modifier
            .fillMaxWidth()
            // Ensures this composable is placed above the keyboard
            .imePadding()
    ) {
        // If the user has selected Calendar, show the sub-row
        if (selectedAction == ReminderAction.Calendar) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = { /* handle "Today" action */ }) {
                    Text("Today")
                }
                TextButton(onClick = { /* handle "Tomorrow" action */ }) {
                    Text("Tomorrow")
                }
                TextButton(onClick = { /* handle "Next Weekend" action */ }) {
                    Text("Next Weekend")
                }
                TextButton(onClick = { /* handle "Date & Time" action */ }) {
                    Text("Date & Time")
                }
            }
        }

        // Main row of icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = { onActionSelected(ReminderAction.Calendar) }) {
                Icon(
                    imageVector = Icons.Default.DateRange, contentDescription = "Calendar"
                )
            }
            IconButton(onClick = { onActionSelected(ReminderAction.Location) }) {
                Icon(
                    imageVector = Icons.Default.LocationOn, contentDescription = "Location"
                )
            }
            IconButton(onClick = { onActionSelected(ReminderAction.Tag) }) {
                Icon(
                    imageVector = Icons.Default.Warning, contentDescription = "Tag"
                )
            }
            IconButton(onClick = { onActionSelected(ReminderAction.Favourite) }) {
                Icon(
                    imageVector = Icons.Default.Favorite, contentDescription = "Favourite"
                )
            }
            IconButton(onClick = { onActionSelected(ReminderAction.Camera) }) {
                Icon(
                    imageVector = Icons.Default.Person, contentDescription = "Camera"
                )
            }
        }
    }
}
