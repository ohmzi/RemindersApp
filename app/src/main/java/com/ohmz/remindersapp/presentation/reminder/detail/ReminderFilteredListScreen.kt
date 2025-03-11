package com.ohmz.remindersapp.presentation.reminder.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.ohmz.remindersapp.presentation.reminder.add.AddReminderScreen
import com.ohmz.remindersapp.presentation.reminder.list.ReminderListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderFilteredListScreen(
    reminderType: ReminderType,
    onNavigateBack: () -> Unit,
    navigateToAddReminder: () -> Unit,
    viewModel: ReminderListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // State for showing the bottom sheet
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Set the selected type in the viewModel
    LaunchedEffect(reminderType) {
        viewModel.selectReminderType(reminderType)
    }

    // Show error in a snackbar if one exists
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearError()
            }
        }
    }

    // Get the title and icon based on the type
    val (screenTitle, screenIcon) = when (reminderType) {
        ReminderType.TODAY -> Pair("Today", Icons.Default.DateRange)
        ReminderType.SCHEDULED -> Pair("Scheduled", Icons.Default.DateRange)
        ReminderType.ALL -> Pair("All", Icons.Default.List)
        ReminderType.FLAGGED -> Pair("Flagged", Icons.Default.Favorite)
        ReminderType.COMPLETED -> Pair("Completed", Icons.Default.CheckCircle)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenTitle) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Add action icons as needed
                    IconButton(onClick = {
                        // Open the bottom sheet instead of navigating
                        showBottomSheet = true
                        coroutineScope.launch { sheetState.show() }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Reminder"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF2F2F7)) // iOS-style light background
            ) {
                // Current reminders list based on selected type
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .background(Color.White, shape = MaterialTheme.shapes.medium)
                ) {
                    val filteredReminders = viewModel.getFilteredReminders()

                    if (filteredReminders.isEmpty()) {
                        // Show empty state with icon
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = screenIcon,
                                contentDescription = null,
                                tint = Color.Gray.copy(alpha = 0.6f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No $screenTitle Reminders",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap + to add a new reminder",
                                color = Color.Gray.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 8.dp)
                        ) {
                            items(filteredReminders) { reminder ->
                                ReminderItem(
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