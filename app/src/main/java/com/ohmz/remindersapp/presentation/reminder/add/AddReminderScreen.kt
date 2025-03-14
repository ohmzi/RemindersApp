package com.ohmz.remindersapp.presentation.reminder.add

import AccessoryBar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.ohmz.remindersapp.domain.model.ReminderAction
import com.ohmz.remindersapp.presentation.common.components.DateTimePicker
import com.ohmz.remindersapp.presentation.common.components.TitleNotesCard
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderScreen(
    onNavigateBack: () -> Unit, viewModel: AddReminderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var showDateTimePicker by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }

    // Function to handle navigation back with unsaved changes check
    val handleNavigateBack = {
        if (uiState.isModified) {
            // Show confirmation dialog if there are unsaved changes
            showDiscardDialog = true
        } else {
            // No unsaved changes, go back directly
            onNavigateBack()
        }
    }

    // Show the keyboard automatically when the screen is shown
    LaunchedEffect(Unit) {
        keyboardController?.show()
    }

    // Handle error and success states
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccess()
            onNavigateBack()
        }
    }

    // Show discard changes dialog if needed
    if (showDiscardDialog) {
        DiscardChangesDialog(onDiscardChanges = {
            showDiscardDialog = false
            viewModel.resetState()
            onNavigateBack()
        }, onCancelDialog = {
            showDiscardDialog = false
            // User chose to continue editing
        })
    }

    Scaffold(
        topBar = {},
        bottomBar = {
            AccessoryBar(
                selectedAction = uiState.selectedAction,
                onActionSelected = { action ->
                    viewModel.toggleAction(action)
                },
                // Date handling
                onTodaySelected = {
                    // Set due date to today at 11:59 PM
                    val today = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                    }.time
                    viewModel.updateDueDate(today)
                    // Deselect calendar to hide the date selector
                    if (uiState.selectedAction == ReminderAction.CALENDAR) {
                        viewModel.toggleAction(ReminderAction.CALENDAR)
                    }
                },
                onTomorrowSelected = {
                    // Set due date to tomorrow at 11:59 PM
                    val tomorrow = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR, 1)
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                    }.time
                    viewModel.updateDueDate(tomorrow)
                    // Deselect calendar to hide the date selector
                    if (uiState.selectedAction == ReminderAction.CALENDAR) {
                        viewModel.toggleAction(ReminderAction.CALENDAR)
                    }
                },
                onWeekendSelected = {
                    // Set due date to next weekend (Saturday) at 11:59 PM
                    val calendar = Calendar.getInstance()
                    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                    val daysUntilSaturday = if (dayOfWeek <= Calendar.SATURDAY) {
                        Calendar.SATURDAY - dayOfWeek
                    } else {
                        7 - (dayOfWeek - Calendar.SATURDAY)
                    }
                    calendar.add(Calendar.DAY_OF_YEAR, daysUntilSaturday)
                    calendar.set(Calendar.HOUR_OF_DAY, 23)
                    calendar.set(Calendar.MINUTE, 59)
                    calendar.set(Calendar.SECOND, 59)
                    viewModel.updateDueDate(calendar.time)
                    // Deselect calendar to hide the date selector
                    if (uiState.selectedAction == ReminderAction.CALENDAR) {
                        viewModel.toggleAction(ReminderAction.CALENDAR)
                    }
                },
                onDateTimeSelected = {
                    // Show the date time picker
                    showDateTimePicker = true
                    // Hide the date selector, date will be set in the picker callback
                    if (uiState.selectedAction == ReminderAction.CALENDAR) {
                        viewModel.toggleAction(ReminderAction.CALENDAR)
                    }
                },
                // List handling
                onListSelected = { list ->
                    viewModel.updateList(list)
                    // Deselect LOCATION to hide the list selector
                    if (uiState.selectedAction == ReminderAction.LOCATION) {
                        viewModel.toggleAction(ReminderAction.LOCATION)
                    }
                },
                onAddNewList = { name ->
                    viewModel.addAndSelectList(name)
                    // Deselect LOCATION to hide the list selector
                    if (uiState.selectedAction == ReminderAction.LOCATION) {
                        viewModel.toggleAction(ReminderAction.LOCATION)
                    }
                },
                availableLists = uiState.availableLists,
                selectedListId = uiState.listId,
                // Priority handling
                onLowPrioritySelected = {
                    viewModel.updatePriority(com.ohmz.remindersapp.domain.model.Priority.LOW)
                    // Deselect TAG to hide the priority selector
                    if (uiState.selectedAction == ReminderAction.TAG) {
                        viewModel.toggleAction(ReminderAction.TAG)
                    }
                },
                onMediumPrioritySelected = {
                    viewModel.updatePriority(com.ohmz.remindersapp.domain.model.Priority.MEDIUM)
                    // Deselect TAG to hide the priority selector
                    if (uiState.selectedAction == ReminderAction.TAG) {
                        viewModel.toggleAction(ReminderAction.TAG)
                    }
                },
                onHighPrioritySelected = {
                    viewModel.updatePriority(com.ohmz.remindersapp.domain.model.Priority.HIGH)
                    // Deselect TAG to hide the priority selector
                    if (uiState.selectedAction == ReminderAction.TAG) {
                        viewModel.toggleAction(ReminderAction.TAG)
                    }
                },
                // Other parameters
                hasDate = uiState.dueDate != null,
                dueDate = uiState.dueDate,
                currentPriority = uiState.priority,
                isFavorite = uiState.isFavorite
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // iOS-like top row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp) // Increased height
                    .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: "Cancel" button with larger text
                TextButton(onClick = handleNavigateBack) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp // Increased by ~2dp
                    )
                }

                // Center: "New Reminder" title
                Text(
                    text = "New Reminder",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp // Ensure title is appropriately sized
                    ),
                    textAlign = TextAlign.Center
                )

                // Right: "Add" button with larger text
                TextButton(
                    onClick = { viewModel.saveReminder() }, enabled = uiState.title.isNotBlank()
                ) {
                    val textColor =
                        if (uiState.title.isNotBlank()) MaterialTheme.colorScheme.primary
                        else Color.Gray

                    Text(
                        text = "Add",
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor,
                        fontSize = 18.sp // Increased by ~2dp
                    )
                }
            }

            // TitleNotesCard for user input
            TitleNotesCard(
                title = uiState.title,
                onTitleChange = { viewModel.updateTitle(it) },
                notes = uiState.notes,
                onNotesChange = { viewModel.updateNotes(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // The list selector is now handled in AccessoryBar with the new style

            // Handle favorite selection
            if (uiState.selectedAction == ReminderAction.FAVORITE) {
                viewModel.toggleFavorite()
                // Auto-deselect favorite after toggling
                viewModel.toggleAction(ReminderAction.FAVORITE)
            }
        }
    }

// Show DateTimePicker if needed
    if (showDateTimePicker) {
        DateTimePicker(initialDate = uiState.dueDate ?: Date(),
            onDateTimeSelected = { selectedDateTime ->
                viewModel.updateDueDate(selectedDateTime)
                showDateTimePicker = false
                // The calendar icon will be blue because hasDate = true, but date selector will be hidden
            },
            onDismiss = {
                showDateTimePicker = false
            })
    }
}


@Composable
private fun PriorityButton(
    text: String, isSelected: Boolean, onClick: () -> Unit
) {
    TextButton(
        onClick = onClick, modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun DiscardChangesDialog(
    onDiscardChanges: () -> Unit, onCancelDialog: () -> Unit
) {
    Dialog(onDismissRequest = onCancelDialog) {
        Surface(
            shape = RoundedCornerShape(16.dp), color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Discard Changes?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "You have unsaved changes that will be lost if you discard this reminder.",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Discard Changes Button (Red)
                TextButton(
                    onClick = onDiscardChanges,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Discard Changes",
                        color = Color.Red,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                }

                // Cancel Button (Blue)
                TextButton(
                    onClick = onCancelDialog,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}