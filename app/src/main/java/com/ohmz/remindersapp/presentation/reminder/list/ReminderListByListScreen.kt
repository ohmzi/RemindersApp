package com.ohmz.remindersapp.presentation.reminder.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ohmz.remindersapp.presentation.common.components.AndroidStyleTopBar
import com.ohmz.remindersapp.presentation.reminder.add.AddReminderScreen
import com.ohmz.remindersapp.presentation.reminder.add.AddReminderViewModel
import com.ohmz.remindersapp.presentation.reminder.detail.ScheduledReminderItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Screen that displays reminders from a specific list with a consistent iOS-style appearance
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListByListScreen(
    listId: Int,
    listName: String,
    onNavigateBack: () -> Unit,
    viewModel: ReminderListViewModel = hiltViewModel(),
    listColor: Color = Color(0xFF007AFF) // Default iOS blue
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Create add reminder viewModel
    val addReminderViewModel: AddReminderViewModel = hiltViewModel()

    // Filter reminders by the listId
    val remindersInList = uiState.reminders.filter { it.listId == listId }

    // Colors for UI
    val iosWhite = Color.White
    val iosBlue = Color(0xFF007AFF)
    // Create a very light version of the list color for the background
    // If the list color is default blue, use white background instead
    val listBackgroundColor = if (listColor == Color(0xFF007AFF)) {
        Color.White
    } else {
        listColor.copy(alpha = 0.1f)
    }

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

    // Inside ReminderListByListScreen.kt, replace the current top bar implementation with:

    Scaffold(containerColor = listBackgroundColor,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // No pre-selection here, it's now handled in LaunchedEffect
                    // Just show the bottom sheet
                    addReminderViewModel.resetState() // This will be overridden by LaunchedEffect

                    showBottomSheet = true
                    coroutineScope.launch { sheetState.show() }
                }, containerColor = listColor, contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add, contentDescription = "Add Reminder"
                )
            }
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Use the Android-style top bar without add button (using FAB instead)
            AndroidStyleTopBar(
                title = listName,
                titleColor = listColor,
                onBackClick = onNavigateBack,
                showAddButton = false, // No add button, using FAB instead
                iconTint = iosBlue
            )

            // Very subtle divider
            //   HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE5E5EA))

            //   HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE5E5EA))


            // Content area
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = listColor.copy(alpha = 0.8f))
                }
            } else if (remindersInList.isEmpty()) {
                // Show empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 32.dp, bottom = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No reminders in this list", color = Color.Gray, fontSize = 16.sp
                    )
                }
            } else {
                // Show list of reminders using the same style as ReminderFilteredListScreen
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(remindersInList) { reminder ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp)
                                .background(listBackgroundColor)
                        ) {
                            ScheduledReminderItem(reminder = reminder,
                                onCheckedChange = { isChecked ->
                                    viewModel.toggleReminderCompletion(reminder)
                                },
                                onDeleteClick = {
                                    viewModel.deleteReminder(reminder)
                                },
                                onFavoriteToggle = { isFavorite ->
                                    viewModel.toggleReminderFavorite(reminder, isFavorite)
                                })
                        }
                    }

                    // Add some bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Bottom sheet for adding a new reminder
    if (showBottomSheet) {
        // Use LaunchedEffect to apply the pre-selections when the sheet appears
        LaunchedEffect(showBottomSheet) {
            // First reset the state
            addReminderViewModel.resetState()
            // Then apply specific pre-selections
            delay(100) // Short delay to ensure resetState completes

            // Create a minimal ReminderList object with the current ID and name
            val currentList = com.ohmz.remindersapp.domain.model.ReminderList(
                id = listId, name = listName
            )
            addReminderViewModel.updateList(currentList)
        }
        ModalBottomSheet(onDismissRequest = {
            coroutineScope.launch {
                sheetState.hide()
                // Reset state when dismissing to ensure a fresh start next time
                addReminderViewModel.resetState()
            }
            showBottomSheet = false
        }, sheetState = sheetState, dragHandle = {}) {
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
                            // Reset state when canceling to ensure a fresh start next time
                            addReminderViewModel.resetState()
                        }
                    }, viewModel = addReminderViewModel
                )
            }
        }
    }
}