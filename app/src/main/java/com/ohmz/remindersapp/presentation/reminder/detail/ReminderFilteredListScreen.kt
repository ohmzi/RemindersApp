package com.ohmz.remindersapp.presentation.reminder.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ohmz.remindersapp.domain.model.Priority
import com.ohmz.remindersapp.domain.model.Reminder
import com.ohmz.remindersapp.domain.model.ReminderAction
import com.ohmz.remindersapp.domain.model.ReminderType
import com.ohmz.remindersapp.presentation.common.components.AndroidStyleTopBar
import com.ohmz.remindersapp.presentation.common.theme.IOSColors
import com.ohmz.remindersapp.presentation.common.utils.DateUtils
import com.ohmz.remindersapp.presentation.common.utils.DateUtils.pastDue
import com.ohmz.remindersapp.presentation.reminder.add.AddReminderScreen
import com.ohmz.remindersapp.presentation.reminder.add.AddReminderViewModel
import com.ohmz.remindersapp.presentation.reminder.list.ReminderListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    // Create add reminder viewModel
    val addReminderViewModel: AddReminderViewModel = hiltViewModel()

    // Show error in a snackbar if one exists
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearError()
            }
        }
    }

    // Get theme colors
    val appColors = com.ohmz.remindersapp.presentation.common.theme.AppTheme
    
    // Get the title, icon and color based on the type
    val (screenTitle, themeColor) = when (reminderType) {
        ReminderType.TODAY -> Pair("Today", appColors.todayColor)
        ReminderType.SCHEDULED -> Pair("Scheduled", appColors.scheduledColor)
        ReminderType.ALL -> Pair("All", appColors.allColor)
        ReminderType.FAVOURITE -> Pair("Favourite", appColors.favoriteColor)
        ReminderType.COMPLETED -> Pair("Completed", appColors.completedColor)
    }
    
    // Get screen icon based on the type
    val screenIcon = when (reminderType) {
        ReminderType.TODAY, ReminderType.SCHEDULED -> Icons.Default.DateRange
        ReminderType.FAVOURITE -> Icons.Default.Favorite
        ReminderType.COMPLETED -> Icons.Default.Check 
        else -> Icons.Default.List
    }

    // Inside ReminderFilteredListScreen.kt, replace the current top bar implementation with:

    Scaffold(containerColor = appColors.mainBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            // Only show FAB if not on the Completed screen
            if (reminderType != ReminderType.COMPLETED) {
                FloatingActionButton(
                    onClick = {
                        // No pre-selection here, it's now handled in LaunchedEffect
                        // Just show the bottom sheet
                        addReminderViewModel.resetState() // This will be overridden by LaunchedEffect

                        showBottomSheet = true
                        coroutineScope.launch { sheetState.show() }
                    }, containerColor = themeColor, contentColor = IOSColors.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add, contentDescription = "Add Reminder"
                    )
                }
            }
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 10.dp)
        ) {
            // Android style top bar with no add button (now using FAB)
            AndroidStyleTopBar(
                title = screenTitle,
                titleColor = themeColor,
                onBackClick = onNavigateBack,
                showAddButton = false, // No add button, using FAB instead
                iconTint = appColors.todayColor
            )

            // Content area
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = themeColor)
                }
            } else {
                val reminders = viewModel.getFilteredReminders()

                if (reminderType == ReminderType.SCHEDULED) {
                    ScheduledRemindersList(reminders = reminders, onCheckedChange = { reminder ->
                        viewModel.toggleReminderCompletion(reminder)
                    }, onDeleteClick = { reminder ->
                        viewModel.deleteReminder(reminder)
                    }, onFavoriteToggle = { reminder, isFavorite ->
                        viewModel.toggleReminderFavorite(reminder, isFavorite)
                    })
                } else if (reminderType == ReminderType.TODAY) {
                    TodayRemindersList(reminders = reminders, onCheckedChange = { reminder ->
                        viewModel.toggleReminderCompletion(reminder)
                    }, onDeleteClick = { reminder ->
                        viewModel.deleteReminder(reminder)
                    }, onFavoriteToggle = { reminder, isFavorite ->
                        viewModel.toggleReminderFavorite(reminder, isFavorite)
                    })
                } else if (reminderType == ReminderType.ALL) {
                    AllRemindersList(reminders = reminders, onCheckedChange = { reminder ->
                        viewModel.toggleReminderCompletion(reminder)
                    }, onDeleteClick = { reminder ->
                        viewModel.deleteReminder(reminder)
                    }, onFavoriteToggle = { reminder, isFavorite ->
                        viewModel.toggleReminderFavorite(reminder, isFavorite)
                    })
                } else if (reminderType == ReminderType.FAVOURITE) {
                    FavoriteRemindersList(reminders = reminders, onCheckedChange = { reminder ->
                        viewModel.toggleReminderCompletion(reminder)
                    }, onDeleteClick = { reminder ->
                        viewModel.deleteReminder(reminder)
                    }, onFavoriteToggle = { reminder, isFavorite ->
                        viewModel.toggleReminderFavorite(reminder, isFavorite)
                    })
                } else if (reminderType == ReminderType.COMPLETED) {
                    CompletedRemindersList(reminders = reminders, onCheckedChange = { reminder ->
                        viewModel.toggleReminderCompletion(reminder)
                    }, onDeleteClick = { reminder ->
                        viewModel.deleteReminder(reminder)
                    }, onFavoriteToggle = { reminder, isFavorite ->
                        viewModel.toggleReminderFavorite(reminder, isFavorite)
                    }, onClearAllCompleted = {
                        viewModel.clearCompletedReminders()
                    })
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
            // Then apply specific pre-selections based on reminderType
            delay(100) // Short delay to ensure resetState completes

            when (reminderType) {
                ReminderType.FAVOURITE -> {
                    // Force favorite=true for Favourite screen
                    addReminderViewModel.setFavorite(true)
                }

                ReminderType.SCHEDULED -> {
                    // Open calendar for Scheduled screen
                    addReminderViewModel.toggleAction(ReminderAction.CALENDAR)
                }

                ReminderType.TODAY -> {
                    // Set due date to today at 11:59 PM for Today screen
                    val today = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                    }.time
                    addReminderViewModel.updateDueDate(today)
                }

                else -> {
                    // No special pre-selection for other screens
                }
            }
        }

        ModalBottomSheet(onDismissRequest = {
            coroutineScope.launch {
                sheetState.hide()
                // Reset state when closing the sheet to ensure a fresh start next time
                addReminderViewModel.resetState()
            }
            showBottomSheet = false
        }, sheetState = sheetState, dragHandle = {}) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
            ) {
                AddReminderScreen(onNavigateBack = {
                    coroutineScope.launch {
                        sheetState.hide()
                        showBottomSheet = false
                        // Reset state when canceling to ensure a fresh start next time
                        addReminderViewModel.resetState()
                    }
                }, viewModel = addReminderViewModel)
            }
        }
    }
}

@Composable
fun ScheduledRemindersList(
    reminders: List<Reminder>,
    onCheckedChange: (Reminder) -> Unit,
    onDeleteClick: (Reminder) -> Unit,
    onFavoriteToggle: (Reminder, Boolean) -> Unit
) {
    // Current date info for calculations
    val today = Calendar.getInstance()
    val currentYear = today.get(Calendar.YEAR)
    val currentMonth = today.get(Calendar.MONTH)

    // Format for day headers
    val dayFormat = SimpleDateFormat("EEE MMM d", Locale.getDefault())

    // Prepare date sections - use DateUtils for all date filtering
    val pastDueReminders = DateUtils.findPastDueRemindersIncludingCompleted(reminders)
    val todayReminders = DateUtils.findTodayReminders(reminders)
    val tomorrowReminders = DateUtils.findTomorrowReminders(reminders)

    // Current month name
    val currentMonthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(today.time)

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // PAST DUE SECTION - Only show if there are past due reminders
        if (pastDueReminders.isNotEmpty()) {
            // Show the Past Due header
            item {
                Text(
                    text = "Past Due",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = IOSColors.Black,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
                )
            }

            // Group past due reminders by day
            val pastDueByDay = pastDueReminders.groupBy { reminder ->
                val cal = Calendar.getInstance().apply { time = reminder.dueDate!! }
                Pair(cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            }

            // Show each past due day
            pastDueByDay.entries.sortedBy { entry ->
                // Create a numeric value that can be compared (month * 100 + day)
                val month = entry.key.first
                val day = entry.key.second
                month * 100 + day
            }.forEach { (dateKey, dayReminders) ->
                // Show the date
                val firstReminderDate = dayReminders.first().dueDate!!
                item {
                    Text(
                        text = dayFormat.format(firstReminderDate),
                        fontSize = 17.sp,
                        color = IOSColors.Gray,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                    )
                }

                // Show reminders for this date, sorted by priority (HIGH to LOW)
                dayReminders.sortedByDescending { it.priority }.forEach { reminder ->
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                            // No need to specify background here as entire screen is white
                        ) {
                            ScheduledReminderItem(reminder = reminder,
                                onCheckedChange = { onCheckedChange(reminder) },
                                onDeleteClick = { onDeleteClick(reminder) },
                                onFavoriteToggle = { isFavorite ->
                                    onFavoriteToggle(reminder, isFavorite)
                                })
                        }
                    }
                }

                // Add divider after each past due date section
                item {
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE5E5EA))
                }
            }
        }

        // TODAY SECTION
        item {
            Text(
                text = "Today",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color(0xFF000000), // Black on white
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
            )
        }

        if (todayReminders.isNotEmpty()) {
            todayReminders.forEach { reminder ->
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(IOSColors.White)
                    ) {
                        ScheduledReminderItem(reminder = reminder,
                            onCheckedChange = { onCheckedChange(reminder) },
                            onDeleteClick = { onDeleteClick(reminder) },
                            onFavoriteToggle = { isFavorite ->
                                onFavoriteToggle(reminder, isFavorite)
                            })
                    }
                }
            }
        }

        item {
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE5E5EA))
        }

        // TOMORROW SECTION
        item {
            Text(
                text = "Tomorrow",
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                color = IOSColors.Gray,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
            )
        }

        if (tomorrowReminders.isNotEmpty()) {
            tomorrowReminders.forEach { reminder ->
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(IOSColors.White)
                    ) {
                        ScheduledReminderItem(reminder = reminder,
                            onCheckedChange = { onCheckedChange(reminder) },
                            onDeleteClick = { onDeleteClick(reminder) },
                            onFavoriteToggle = { isFavorite ->
                                onFavoriteToggle(reminder, isFavorite)
                            })
                    }
                }
            }
        }

        item {
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE5E5EA))
        }

        // NEXT 5 DAYS
        val nextFiveDays = DateUtils.generateNextFiveDays()

        // Show each of the next 5 days
        nextFiveDays.forEach { date ->
            val cal = Calendar.getInstance().apply { time = date }
            val dateStr = dayFormat.format(date)

            // Find reminders for this date
            val dayReminders = reminders.filter { reminder ->
                reminder.dueDate?.let { dueDate ->
                    val dueCal = Calendar.getInstance().apply { time = dueDate }
                    DateUtils.isSameDay(cal, dueCal)
                } ?: false
            }

            // Only show the date header - no empty placeholders
            item {
                Text(
                    text = dateStr,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (dayReminders.isNotEmpty()) IOSColors.Black else IOSColors.Gray,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
                )
            }

            if (dayReminders.isNotEmpty()) {
                // Only show reminders if they exist for this date
                dayReminders.forEach { reminder ->
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(IOSColors.White)
                        ) {
                            ScheduledReminderItem(reminder = reminder,
                                onCheckedChange = { onCheckedChange(reminder) },
                                onDeleteClick = { onDeleteClick(reminder) },
                                onFavoriteToggle = { isFavorite ->
                                    onFavoriteToggle(reminder, isFavorite)
                                })
                        }
                    }
                }

                // Only add divider if there were reminders
                item {
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE5E5EA))
                }
            }
        }

        // REST OF MONTH - just the header, no placeholder
        item {
            Text(
                text = "Rest of $currentMonthName",
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                color = IOSColors.Gray,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
            )
        }

        // FUTURE MONTHS - just headers, no placeholders
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())

        // Generate next 12 months
        for (i in 1..12) {
            val futureCalendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.MONTH, i)
            }

            val futureMonth = futureCalendar.get(Calendar.MONTH)
            val futureYear = futureCalendar.get(Calendar.YEAR)
            val monthName = monthFormat.format(futureCalendar.time)

            // Create month header
            val titleWithYear = if (futureYear != currentYear) {
                "$monthName, $futureYear"
            } else {
                monthName
            }

            // Find reminders for this month
            val monthReminders = reminders.filter { reminder ->
                reminder.dueDate?.let { dueDate ->
                    val dueCal = Calendar.getInstance().apply { time = dueDate }
                    dueCal.get(Calendar.MONTH) == futureMonth && dueCal.get(Calendar.YEAR) == futureYear
                } ?: false
            }

            // Month header with conditional styling
            item {
                Text(
                    text = titleWithYear,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    // Black for months with reminders, Gray for months without
                    color = if (monthReminders.isNotEmpty()) IOSColors.Black else IOSColors.Gray,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
                )
            }

            if (monthReminders.isNotEmpty()) {
                // Group by day
                val byDay = monthReminders.groupBy { reminder ->
                    val cal = Calendar.getInstance().apply { time = reminder.dueDate!! }
                    cal.get(Calendar.DAY_OF_MONTH)
                }

                // Show each day
                byDay.keys.sorted().forEach { day ->
                    val firstReminderDate = byDay[day]!!.first().dueDate!!
                    val dateStr = dayFormat.format(firstReminderDate)

                    item {
                        Text(
                            text = dateStr,
                            fontSize = 17.sp,
                            color = IOSColors.Black,  // Black for days with reminders
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                        )
                    }

                    byDay[day]!!.forEach { reminder ->
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(IOSColors.White)
                            ) {
                                ScheduledReminderItem(reminder = reminder,
                                    onCheckedChange = { onCheckedChange(reminder) },
                                    onDeleteClick = { onDeleteClick(reminder) },
                                    onFavoriteToggle = { isFavorite ->
                                        onFavoriteToggle(reminder, isFavorite)
                                    })
                            }
                        }
                    }

                    item {
                        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE5E5EA))
                    }
                }
            }
        }

        // Add bottom spacing
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun TodayRemindersList(
    reminders: List<Reminder>,
    onCheckedChange: (Reminder) -> Unit,
    onDeleteClick: (Reminder) -> Unit,
    onFavoriteToggle: (Reminder, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp)
    ) {
        // Filter for just today's reminders and show them
        val todayReminders = DateUtils.findTodayReminders(reminders)

        if (todayReminders.isNotEmpty()) {
            todayReminders.forEach { reminder ->
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(IOSColors.White)
                    ) {
                        ScheduledReminderItem(reminder = reminder,
                            onCheckedChange = { onCheckedChange(reminder) },
                            onDeleteClick = { onDeleteClick(reminder) },
                            onFavoriteToggle = { isFavorite ->
                                onFavoriteToggle(reminder, isFavorite)
                            })
                    }
                }
            }
        } else {
            // Enhanced empty state for Today screen
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight(0.85f), // Take most of the available height
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Icon with background circle like iOS
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF007AFF).copy(alpha = 0.1f)), // Today blue color
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = Color(0xFF007AFF),
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No Today Reminders",
                            color = IOSColors.Gray,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tap + to add a new reminder",
                            color = IOSColors.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Add bottom spacing
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun AllRemindersList(
    reminders: List<Reminder>,
    onCheckedChange: (Reminder) -> Unit,
    onDeleteClick: (Reminder) -> Unit,
    onFavoriteToggle: (Reminder, Boolean) -> Unit
) {
    // Custom sorting that keeps past due items in consistent positions regardless of completion status
    val sortedReminders = reminders.sortedWith(
        compareBy<Reminder> { 
            // First sort criteria: is the reminder past due by date (ignoring completion status)
            val isPastDue = it.dueDate?.let { date ->
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val reminderCal = Calendar.getInstance().apply { time = date }
                reminderCal.before(today) // Check if it's past due by date only
            } ?: false
            !isPastDue // Invert so past due comes first
        }
        .thenByDescending { it.priority } // Then by priority (HIGH first)
        .thenBy { it.dueDate } // Then by date ascending for non-past due
        .thenBy { it.isCompleted } // Uncompleted items first within same date & priority
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // If no reminders, show enhanced empty state
        if (sortedReminders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight(0.85f), // Take most of the available height
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Icon with background circle like iOS
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(IOSColors.Black.copy(alpha = 0.1f)), // All reminders: black
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = null,
                                tint = IOSColors.Black,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No Reminders",
                            color = IOSColors.Gray,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tap + to add a new reminder",
                            color = IOSColors.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            // Display all reminders without grouping
            sortedReminders.forEach { reminder ->
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(IOSColors.White)
                    ) {
                        AllReminderItem(reminder = reminder,
                            onCheckedChange = { onCheckedChange(reminder) },
                            onDeleteClick = { onDeleteClick(reminder) },
                            onFavoriteToggle = { isFavorite ->
                                onFavoriteToggle(reminder, isFavorite)
                            })
                    }
                }

                // Add divider after each reminder
                item {
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE5E5EA))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * A special version of reminder item for the All screen that includes the date display
 */
@Composable
fun AllReminderItem(
    reminder: Reminder,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    onFavoriteToggle: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable(onClick = { onCheckedChange(!reminder.isCompleted) })
                .background(if (reminder.isCompleted) Color(0xFF007AFF) else IOSColors.White)
                .border(
                    width = 1.5.dp,
                    color = if (reminder.isCompleted) Color(0xFF007AFF) else Color(0xFFD1D1D6),
                    shape = CircleShape
                ), contentAlignment = Alignment.Center
        ) {
            if (reminder.isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = IOSColors.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Title, notes and date
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Priority color dot - only one dot for the priority
                val dotColor = when (reminder.priority) {
                    Priority.LOW -> Color(0xFF34C759)
                    Priority.MEDIUM -> Color(0xFF007AFF)
                    Priority.HIGH -> Color(0xFFFF3B30)
                }

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Title
                Text(
                    text = reminder.title,
                    fontWeight = FontWeight.Normal,
                    fontSize = 17.sp,
                    color = if (reminder.isCompleted) IOSColors.Gray else IOSColors.Black
                )
            }

            // Notes
            if (!reminder.notes.isNullOrBlank()) {
                Text(
                    text = reminder.notes,
                    fontSize = 14.sp,
                    color = IOSColors.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Date display inside the reminder card
            reminder.dueDate?.let { date ->
                val dateText = when {
                    DateUtils.findTodayReminders(listOf(reminder)).isNotEmpty() -> "Today"
                    DateUtils.findTomorrowReminders(listOf(reminder)).isNotEmpty() -> "Tomorrow"
                    else -> {
                        SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(date)
                    }
                }
                val dateColor = if (pastDue(date)) Color(0xFFFF3B30) else Color(0xFF007AFF)

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateText,
                    fontSize = 13.sp,
                    color = if (reminder.isCompleted) IOSColors.Gray else dateColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Favorite icon
        if (reminder.isFavorite) {
            IconButton(
                onClick = { onFavoriteToggle(false) }, modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = Color(0xFFFF3B30),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Delete icon
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFFF3B30)
            )
        }
    }
}

@Composable
fun FavoriteRemindersList(
    reminders: List<Reminder>,
    onCheckedChange: (Reminder) -> Unit,
    onDeleteClick: (Reminder) -> Unit,
    onFavoriteToggle: (Reminder, Boolean) -> Unit
) {
    // Filter only favorite reminders and sort by due date
    val favoriteReminders = reminders.filter { it.isFavorite }
        .sortedWith(compareBy({ it.dueDate == null }, // null dates last
            { it.dueDate } // then by date
        ))

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp)
    ) {
        if (favoriteReminders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight(0.85f), // Take most of the available height
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Icon with background circle like iOS
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF3B30).copy(alpha = 0.1f)), // Favorite red color
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFFF3B30),
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No Favorite Reminders",
                            color = IOSColors.Gray,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tap + to add a new reminder",
                            color = IOSColors.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            item {}

            favoriteReminders.forEach { reminder ->
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(IOSColors.White)
                    ) {
                        ScheduledReminderItem(reminder = reminder,
                            onCheckedChange = { onCheckedChange(reminder) },
                            onDeleteClick = { onDeleteClick(reminder) },
                            onFavoriteToggle = { isFavorite ->
                                onFavoriteToggle(reminder, isFavorite)
                            })
                    }
                }

                item {
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE5E5EA))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}