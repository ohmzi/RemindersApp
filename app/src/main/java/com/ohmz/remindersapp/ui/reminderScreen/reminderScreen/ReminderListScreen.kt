package com.ohmz.remindersapp.ui.reminderScreen.reminderScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ohmz.remindersapp.data.ReminderCardData
import com.ohmz.remindersapp.data.ReminderEntity
import com.ohmz.remindersapp.ui.reminderScreen.viewModel.RemindersViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReminderListScreen(
    viewModel: RemindersViewModel = hiltViewModel()
) {
    // Collect reminders and card data
    val reminders = viewModel.reminders.collectAsState().value
    val reminderCards = listOf(
        ReminderCardData("Today", 1, Icons.Default.Notifications),
        ReminderCardData("Scheduled", 2, Icons.Default.Star),
        ReminderCardData("All", 15, Icons.Default.Email),
        ReminderCardData("Flagged", 0, Icons.Default.Favorite),
        ReminderCardData("Completed", 5, Icons.Default.Check)
    )

    // State for controlling the bottom sheet
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    // Main Scaffold layout
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Reminders") })
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        // Open bottom sheet when "+ New Reminder" is clicked
                        showBottomSheet = true
                        coroutineScope.launch { sheetState.show() }
                    }
                ) {
                    Text(
                        text = "+ New Reminder",
                        fontSize = 20.sp,
                        color = Color.Blue,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif,
                        fontStyle = FontStyle.Normal
                    )
                }
                TextButton(
                    onClick = {
                        // Open bottom sheet when "Add List" is clicked as well
                        showBottomSheet = true
                        coroutineScope.launch { sheetState.show() }
                    }
                ) {
                    Text(
                        text = "Add List",
                        color = Color.Blue,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        fontStyle = FontStyle.Normal
                    )
                }
            }
        }
    ) { padding ->
        // Example content; you can choose to show either a LazyColumn list or a grid.
        // Here we display the reminder cards in a grid.
        ReminderCardGrid(
            cards = reminderCards,
            onCardClick = { cardData ->
                // Handle navigation or detail display when a card is clicked
            }
        )
    }

    // ModalBottomSheet to display AddReminderScreen as a bottom sheet
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch { sheetState.hide() }
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            // Wrap the AddReminderScreen in a Column with height 90% of the screen
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
            ) {
                AddReminderScreen(
                    onSave = {
                        coroutineScope.launch { sheetState.hide() }
                        showBottomSheet = false
                    },
                    onCancel = {
                        coroutineScope.launch { sheetState.hide() }
                        showBottomSheet = false
                    },
                    viewModel = viewModel
                )
            }
        }
    }

}

@Composable
fun ReminderItem(
    reminder: ReminderEntity,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = reminder.title, style = MaterialTheme.typography.bodyMedium)
                if (!reminder.description.isNullOrBlank()) {
                    Text(text = reminder.description)
                }
                if (reminder.dueDate != null) {
                    Text(text = "Due: ${reminder.dueDate}") // Format the date as needed
                }
            }
            Column {
                Checkbox(
                    checked = reminder.isCompleted,
                    onCheckedChange = { onToggleComplete() }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReminderCardGrid(
    cards: List<ReminderCardData>,
    onCardClick: (ReminderCardData) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(8.dp)
    ) {
        items(cards) { card ->
            ReminderCard(
                data = card,
                onClick = onCardClick
            )
        }
    }
}
