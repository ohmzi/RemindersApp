package com.ohmz.remindersapp.presentation.reminder.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ohmz.remindersapp.domain.model.ReminderType
import com.ohmz.remindersapp.presentation.common.components.ReminderCard
import com.ohmz.remindersapp.presentation.common.components.ReminderCardData
import com.ohmz.remindersapp.presentation.reminder.add.AddReminderScreen
import com.ohmz.remindersapp.presentation.reminder.list.ReminderListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderMainScreen(
    navigateToAddReminder: () -> Unit,
    navigateToFilteredList: (ReminderType) -> Unit,
    viewModel: ReminderListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // State for showing the bottom sheet
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Create reminder category cards
    val reminderCards = listOf(
        ReminderCardData(
            type = ReminderType.TODAY,
            title = "Today",
            count = viewModel.getFilteredReminders().count { reminder ->
                reminder.dueDate?.let { date ->
                    val today = java.util.Calendar.getInstance()
                    val reminderDate = java.util.Calendar.getInstance().apply { time = date }
                    today.get(java.util.Calendar.YEAR) == reminderDate.get(java.util.Calendar.YEAR) &&
                            today.get(java.util.Calendar.DAY_OF_YEAR) == reminderDate.get(java.util.Calendar.DAY_OF_YEAR)
                } ?: false
            },
            icon = Icons.Default.DateRange
        ),
        ReminderCardData(
            type = ReminderType.SCHEDULED,
            title = "Scheduled",
            count = viewModel.getFilteredReminders().count { it.dueDate != null },
            icon = Icons.Default.DateRange
        ),
        ReminderCardData(
            type = ReminderType.ALL,
            title = "All",
            count = viewModel.getFilteredReminders().size,
            icon = Icons.Default.List
        ),
        ReminderCardData(
            type = ReminderType.FLAGGED,
            title = "Flagged",
            count = viewModel.getFilteredReminders().count { it.isFavorite },
            icon = Icons.Default.Favorite
        ),
        ReminderCardData(
            type = ReminderType.COMPLETED,
            title = "Completed",
            count = viewModel.getFilteredReminders().count { it.isCompleted },
            icon = Icons.Default.CheckCircle
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reminders") }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                // Open the bottom sheet instead of navigating
                                showBottomSheet = true
                                coroutineScope.launch { sheetState.show() }
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "New Reminder",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }

                        TextButton(onClick = { /* Show add list dialog */ }) {
                            Text(
                                text = "Add List",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color(0xFFF2F2F7)) // iOS-style light background
            ) {
                // Section title for "My Reminders"
                Text(
                    text = "My Reminders",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )

                // Grid of reminder type cards
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    items(reminderCards) { cardData ->
                        ReminderCard(
                            data = cardData,
                            isSelected = false, // Not using selection on main screen
                            onClick = { type ->
                                // Navigate to filtered list screen
                                navigateToFilteredList(type)
                            }
                        )
                    }
                }

                // Section title for "My Lists"
                Text(
                    text = "My Lists",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                )

                // Placeholder for custom lists
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(horizontal = 16.dp)
                        .background(Color.White, shape = MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Custom lists will appear here",
                        color = Color.Gray
                    )
                }
            }
        }
    }

    // Bottom sheet for adding a new reminder
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch { sheetState.hide() }
                showBottomSheet = false
            },
            sheetState = sheetState,
            dragHandle = {}
        ) {
            // Wrap the AddReminderScreen in a Column with height 90% of the screen
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
            ) {
                AddReminderScreen(
                    onNavigateBack = {
                        coroutineScope.launch {
                            sheetState.hide()
                            showBottomSheet = false
                        }
                    }
                )
            }
        }
    }
}