package com.ohmz.remindersapp.presentation.reminder.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ohmz.remindersapp.presentation.reminder.add.AddReminderScreen
import com.ohmz.remindersapp.presentation.reminder.detail.ScheduledReminderItem
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

    Scaffold(
        containerColor = listBackgroundColor,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Minimalist iOS-style top bar with significantly reduced padding
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, bottom = 0.dp) // Reduced padding to match iOS style
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

                // Add button
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Reminder",
                    tint = iosBlue,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .size(24.dp)
                        .clickable {
                            showBottomSheet = true
                            coroutineScope.launch { sheetState.show() }
                        }
                )
            }

            // Very subtle divider
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE5E5EA))

            // Large title with the list color - matching ReminderFilteredListScreen style
            Text(
                text = listName,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                color = listColor,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            // Content area
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = listColor.copy(alpha = 0.8f))
                }
            } else if (remindersInList.isEmpty()) {
                // Show empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, bottom = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No reminders in this list",
                        color = Color.Gray,
                        fontSize = 16.sp
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
                                .background(listBackgroundColor)
                        ) {
                            ScheduledReminderItem(
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