package com.ohmz.remindersapp.presentation.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ohmz.remindersapp.domain.model.ReminderAction

@Composable
fun AccessoryBar(
    selectedAction: ReminderAction?,
    onActionSelected: (ReminderAction) -> Unit,
    modifier: Modifier = Modifier,
    onTodaySelected: () -> Unit = {},
    onTomorrowSelected: () -> Unit = {},
    onWeekendSelected: () -> Unit = {},
    onDateTimeSelected: () -> Unit = {}
) {
    // A column that can contain both the "sub-row" and the main row
    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        // If the user has selected Calendar, show the sub-row
        if (selectedAction == ReminderAction.CALENDAR) {
            CalendarSubOptions(
                onTodaySelected = onTodaySelected,
                onTomorrowSelected = onTomorrowSelected,
                onWeekendSelected = onWeekendSelected,
                onDateTimeSelected = onDateTimeSelected
            )
        }

        // Main row of icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = { onActionSelected(ReminderAction.CALENDAR) }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Calendar",
                    tint = if (selectedAction == ReminderAction.CALENDAR)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.Gray
                )
            }
            IconButton(onClick = { onActionSelected(ReminderAction.LOCATION) }) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = if (selectedAction == ReminderAction.LOCATION)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.Gray
                )
            }
            IconButton(onClick = { onActionSelected(ReminderAction.TAG) }) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Tag",
                    tint = if (selectedAction == ReminderAction.TAG)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.Gray
                )
            }
            IconButton(onClick = { onActionSelected(ReminderAction.FAVORITE) }) {
                Icon(
                    imageVector = if (selectedAction == ReminderAction.FAVORITE)
                        Icons.Default.Favorite
                    else
                        Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (selectedAction == ReminderAction.FAVORITE)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.Gray
                )
            }
            IconButton(onClick = { onActionSelected(ReminderAction.CAMERA) }) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "Camera",
                    tint = if (selectedAction == ReminderAction.CAMERA)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.Gray
                )
            }
        }
    }
}

@Composable
private fun CalendarSubOptions(
    onTodaySelected: () -> Unit,
    onTomorrowSelected: () -> Unit,
    onWeekendSelected: () -> Unit,
    onDateTimeSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TextButton(onClick = onTodaySelected) {
            Text("Today", color = MaterialTheme.colorScheme.primary)
        }
        TextButton(onClick = onTomorrowSelected) {
            Text("Tomorrow", color = MaterialTheme.colorScheme.primary)
        }
        TextButton(onClick = onWeekendSelected) {
            Text("Next Weekend", color = MaterialTheme.colorScheme.primary)
        }
        TextButton(onClick = onDateTimeSelected) {
            Text("Date & Time", color = MaterialTheme.colorScheme.primary)
        }
    }
}
