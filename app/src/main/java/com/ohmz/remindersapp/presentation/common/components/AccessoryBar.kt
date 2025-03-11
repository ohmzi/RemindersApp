package com.ohmz.remindersapp.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.WindowInsets
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .imePadding() // This ensures the bar stays above the keyboard 
            .windowInsetsPadding(WindowInsets.navigationBars) // For navigation bar
            .wrapContentHeight() // Force the bar to wrap its content
    ) {
        // If calendar is selected, show the date selector
        if (selectedAction == ReminderAction.CALENDAR) {
            DateSelector(
                onTodaySelected = onTodaySelected,
                onTomorrowSelected = onTomorrowSelected,
                onNextWeekendSelected = onWeekendSelected,
                onDateTimeSelected = onDateTimeSelected
            )
        }

        // Main action bar with icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .background(Color.White),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Calendar icon
            IconButton(
                onClick = { onActionSelected(ReminderAction.CALENDAR) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Calendar",
                    tint = if (selectedAction == ReminderAction.CALENDAR)
                        Color(0xFF0000FF) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Location icon
            IconButton(
                onClick = { onActionSelected(ReminderAction.LOCATION) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = if (selectedAction == ReminderAction.LOCATION)
                        Color(0xFF0000FF) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Tag/Priority icon
            IconButton(
                onClick = { onActionSelected(ReminderAction.TAG) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Priority",
                    tint = if (selectedAction == ReminderAction.TAG)
                        Color(0xFF0000FF) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Favorite icon
            IconButton(
                onClick = { onActionSelected(ReminderAction.FAVORITE) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (selectedAction == ReminderAction.FAVORITE)
                        Icons.Default.Favorite
                    else
                        Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (selectedAction == ReminderAction.FAVORITE)
                        Color(0xFF0000FF) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Person/Photo icon
            IconButton(
                onClick = { onActionSelected(ReminderAction.CAMERA) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "Photo",
                    tint = if (selectedAction == ReminderAction.CAMERA)
                        Color(0xFF0000FF) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}