package com.ohmz.remindersapp.presentation.reminder.detail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ohmz.remindersapp.domain.model.ReminderType
import com.ohmz.remindersapp.presentation.common.components.EnhancedReminderItem
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

    // Get the title, icon and color based on the type
    val (screenTitle, screenIcon, themeColor) = when (reminderType) {
        ReminderType.TODAY -> Triple("Today", Icons.Default.DateRange, Color(0xFF007AFF))
        ReminderType.SCHEDULED -> Triple("Scheduled", Icons.Default.DateRange, Color(0xFFFF9500)) // Changed to orange
        ReminderType.ALL -> Triple("All", Icons.Default.List, Color(0xFF000000))
        ReminderType.FAVOURITE -> Triple("Favourite", Icons.Default.Favorite, Color(0xFFFF3B30)) // Changed to red with heart icon
        ReminderType.COMPLETED -> Triple("Completed", Icons.Default.CheckCircle, Color(0xFF8E8E93))
    }

    // iOS light background color
    val iosBackgroundColor = Color(0xFFF2F2F7)
    val iosBlue = Color(0xFF007AFF)

    Scaffold(containerColor = iosBackgroundColor,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                // Add status bar padding to avoid content spilling into status bar
                .statusBarsPadding()
        ) {
            // Custom top bar with more iOS-like styling
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Back button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable(onClick = onNavigateBack)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = iosBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Screen title
                Text(
                    text = screenTitle,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Add button
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = "Add Reminder",
                    tint = iosBlue,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(24.dp)
                        .clickable {
                            showBottomSheet = true
                            coroutineScope.launch { sheetState.show() }
                        })
            }

            // Content area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = themeColor)
                    }
                } else {
                    val filteredReminders = viewModel.getFilteredReminders()

                    if (filteredReminders.isEmpty()) {
                        // Enhanced empty state with iOS styling
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
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
                                        .background(themeColor.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = screenIcon,
                                        contentDescription = null,
                                        tint = themeColor,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "No $screenTitle Reminders",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Tap + to add a new reminder",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        // Enhanced card container for the list
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredReminders) { reminder ->
                                    EnhancedReminderItem(
                                        reminder = reminder, 
                                        onCheckedChange = { isChecked ->
                                            viewModel.toggleReminderCompletion(reminder)
                                        }, 
                                        onDeleteClick = {
                                            viewModel.deleteReminder(reminder)
                                        },
                                        onFavoriteToggle = { isFavorite ->
                                            viewModel.toggleReminderFavorite(reminder, isFavorite)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom spacing to avoid content being hidden by system navigation
            Spacer(modifier = Modifier.navigationBarsPadding())
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