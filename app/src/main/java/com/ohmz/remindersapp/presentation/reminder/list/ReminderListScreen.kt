package com.ohmz.remindersapp.presentation.reminder.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.ohmz.remindersapp.ReminderItem
import com.ohmz.remindersapp.domain.model.ReminderType
import com.ohmz.remindersapp.presentation.common.components.EnhancedReminderItem
import com.ohmz.remindersapp.presentation.common.components.ReminderCard
import com.ohmz.remindersapp.presentation.common.components.ReminderCardData
import com.ohmz.remindersapp.presentation.reminder.add.AddReminderScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListScreen(
    navigateToAddReminder: () -> Unit = {},
    viewModel: ReminderListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // State for showing the bottom sheet
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Show error in a snackbar if one exists
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearError()
            }
        }
    }

    // Create our reminderCards data
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
            icon = Icons.Default.Notifications
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
            type = ReminderType.FAVOURITE,
            title = "Flagged",
            count = viewModel.getFilteredReminders().count { it.isFavorite },
            icon = Icons.Default.Favorite
        ),
        ReminderCardData(
            type = ReminderType.COMPLETED,
            title = "Completed",
            count = viewModel.getFilteredReminders().count { it.isCompleted },
            icon = Icons.Default.Check
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reminders") }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
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
                    .statusBarsPadding() // Add padding for status bar
                    .navigationBarsPadding() // Add padding for navigation bar
                    .background(Color(0xFFF2F2F7)) // iOS-style light background
            ) {
                // Grid of reminder type cards
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    items(reminderCards) { cardData ->
                        ReminderCard(
                            data = cardData,
                            isSelected = uiState.selectedType == cardData.type,
                            onClick = { type ->
                                viewModel.selectReminderType(type)
                            }
                        )
                    }
                }

                // Current reminders list based on selected type
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .background(Color.White, shape = MaterialTheme.shapes.medium)
                        .padding(vertical = 4.dp)
                ) {
                    val filteredReminders = viewModel.getFilteredReminders()

                    if (filteredReminders.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No reminders",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 8.dp)
                        ) {
                            items(filteredReminders) { reminder ->
                                EnhancedReminderItem(
                                    reminder = reminder,
                                    onToggleComplete = {
                                        viewModel.toggleReminderCompletion(reminder)
                                    },
                                    onDelete = {
                                        viewModel.deleteReminder(reminder)
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
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