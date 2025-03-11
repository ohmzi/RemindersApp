package com.ohmz.remindersapp.presentation.reminder.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ohmz.remindersapp.domain.model.ReminderType
import com.ohmz.remindersapp.presentation.common.components.ReminderCategoryCardAlt
import com.ohmz.remindersapp.presentation.common.components.EnhancedListItem
import com.ohmz.remindersapp.presentation.reminder.add.AddReminderScreen
import com.ohmz.remindersapp.presentation.reminder.list.ReminderListViewModel
import kotlinx.coroutines.launch
import com.ohmz.remindersapp.presentation.common.components.ReminderCategoryData

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

    // Background color that matches iOS light gray
    val iosBackgroundColor = Color(0xFFF2F2F7)
    val iosBlue = Color(0xFF007AFF)

    // Define colors for each category
    val todayColor = Color(0xFF007AFF) // iOS blue
    val scheduledColor = Color(0xFFFF3B30) // iOS red
    val allColor = Color(0xFF000000) // Black
    val flaggedColor = Color(0xFFFF9500) // iOS orange
    val completedColor = Color(0xFF8E8E93) // iOS gray

    // Create reminder category data with iOS-like icons and colors
    val reminderCategories = listOf(
        ReminderCategoryData(
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
            color = todayColor,
            icon = Icons.Default.DateRange
        ),
        ReminderCategoryData(
            type = ReminderType.SCHEDULED,
            title = "Scheduled",
            count = viewModel.getFilteredReminders().count { it.dueDate != null },
            color = scheduledColor,
            icon = Icons.Default.DateRange
        ),
        ReminderCategoryData(
            type = ReminderType.ALL,
            title = "All",
            count = viewModel.getFilteredReminders().size,
            color = allColor,
            icon = Icons.Default.List
        ),
        ReminderCategoryData(
            type = ReminderType.FLAGGED,
            title = "Flagged",
            count = viewModel.getFilteredReminders().count { it.isFavorite },
            color = flaggedColor,
            icon = Icons.Default.Warning
        ),
        ReminderCategoryData(
            type = ReminderType.COMPLETED,
            title = "Completed",
            count = viewModel.getFilteredReminders().count { it.isCompleted },
            color = completedColor,
            icon = Icons.Default.CheckCircle
        )
    )

    Scaffold(
        containerColor = iosBackgroundColor,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                // Add status bar padding to avoid content spilling into status bar
                .statusBarsPadding()
        ) {
            // iOS-style Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // iOS-height search bar (36dp) with subtle shadow
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .shadow(
                            elevation = 1.dp,
                            shape = RoundedCornerShape(10.dp),
                            clip = true
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE3E3E8)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF8E8E93),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Search",
                            color = Color(0xFF8E8E93),
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Main content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                // Grid of reminder categories with improved spacing and shadows
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(reminderCategories) { category ->
                        // Use the updated card with consistent rounded shadows
                        ReminderCategoryCardAlt(
                            category = category,
                            onClick = { navigateToFilteredList(category.type) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // "My Lists" title like iOS
                Text(
                    text = "My Lists",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Lists section with consistent shadow
                EnhancedListItem(
                    title = "Reminders",
                    count = viewModel.getFilteredReminders().size,
                    icon = Icons.Default.List,
                    iconBackgroundColor = Color(0xFFFF9500),
                    onClick = { /* Show all reminders */ }
                )
            }

            // Bottom bar with buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(iosBackgroundColor)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    // Add navigation bar padding to avoid content hiding behind system navigation
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // New Reminder button with iOS-style
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    showBottomSheet = true
                                    coroutineScope.launch { sheetState.show() }
                                }
                                .padding(end = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(iosBlue)
                                    .shadow(
                                        elevation = 2.dp,
                                        shape = CircleShape,
                                        clip = false
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "New Reminder",
                                color = iosBlue,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // Add List button (iOS style)
                    Text(
                        text = "Add List",
                        color = iosBlue,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clickable { /* Show add list dialog */ }
                            .padding(8.dp) // Add some padding for the touch target
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