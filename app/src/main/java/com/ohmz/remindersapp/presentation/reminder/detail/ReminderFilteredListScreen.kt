package com.ohmz.remindersapp.presentation.reminder.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ohmz.remindersapp.domain.model.Reminder
import com.ohmz.remindersapp.domain.model.ReminderType
import com.ohmz.remindersapp.presentation.common.utils.DateUtils
import com.ohmz.remindersapp.presentation.reminder.add.AddReminderScreen
import com.ohmz.remindersapp.presentation.reminder.list.ReminderListViewModel
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

    // Show error in a snackbar if one exists
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearError()
            }
        }
    }

    // Get the title, icon and color based on the type
    val (screenTitle, themeColor) = when (reminderType) {
        ReminderType.TODAY -> Pair("Today", Color(0xFF007AFF))
        ReminderType.SCHEDULED -> Pair("Scheduled", Color(0xFFFF3B30)) // Red color for scheduled
        ReminderType.ALL -> Pair("All", Color(0xFF000000))
        ReminderType.FAVOURITE -> Pair("Favourite", Color(0xFFFF3B30))
        ReminderType.COMPLETED -> Pair("Completed", Color(0xFF8E8E93))
    }

    // iOS colors
    val iosWhiteBackground = Color.White // Pure white background like in iOS
    val iosBlue = Color(0xFF007AFF)

    Scaffold(containerColor = iosWhiteBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Minimalist iOS-style top bar with significantly reduced padding
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, bottom = 0.dp) // Removed nearly all padding
            ) {
                // Back button with ONLY arrow (no text)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .size(44.dp) // Large tappable area
                        .clickable(onClick = onNavigateBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = iosBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // No centered title

                // Add button
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = "Add Reminder",
                    tint = iosBlue,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .size(24.dp)
                        .clickable {
                            showBottomSheet = true
                            coroutineScope.launch { sheetState.show() }
                        })
            }

            // Very subtle divider for white background
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE5E5EA))

            // Large title for Scheduled only
            if (reminderType == ReminderType.SCHEDULED) {
                Text(
                    text = screenTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    color = themeColor,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }

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
                } else {
                    // Other reminder type screens
                    // (Standard list implementation not shown)
                }
            }
        }
    }

    // Bottom sheet for adding a new reminder
    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = {
            coroutineScope.launch { sheetState.hide() }
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
                    }
                })
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

    // Prepare date sections
    val pastDueReminders = DateUtils.findPastDueReminders(reminders)
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
                    color = Color.Black,
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
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                    )
                }

                // Show reminders for this date
                dayReminders.forEach { reminder ->
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
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
                            .background(Color.White)
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
                color = Color.Gray,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
            )
        }

        if (tomorrowReminders.isNotEmpty()) {
            tomorrowReminders.forEach { reminder ->
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
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
                    color = if (dayReminders.isNotEmpty()) Color.Black else Color.Gray,
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
                                .background(Color.White)
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
                color = Color.Gray,
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
                    color = if (monthReminders.isNotEmpty()) Color.Black else Color.Gray,
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
                            color = Color.Black,  // Black for days with reminders
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                        )
                    }

                    byDay[day]!!.forEach { reminder ->
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
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