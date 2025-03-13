package com.ohmz.remindersapp.presentation.reminder.main

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.ohmz.remindersapp.domain.model.ReminderType
import com.ohmz.remindersapp.presentation.common.components.EnhancedListItem
import com.ohmz.remindersapp.presentation.common.components.ReminderCategoryCardAlt
import com.ohmz.remindersapp.presentation.common.components.ReminderCategoryData
import com.ohmz.remindersapp.presentation.navigation.Screen
import com.ohmz.remindersapp.presentation.reminder.add.AddReminderScreen
import com.ohmz.remindersapp.presentation.reminder.add.AddReminderViewModel
import com.ohmz.remindersapp.presentation.reminder.list.ReminderListViewModel
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderMainScreen(
    navigateToAddReminder: () -> Unit,
    navigateToFilteredList: (ReminderType) -> Unit,
    viewModel: ReminderListViewModel = hiltViewModel(),
    mainViewModel: ReminderMainViewModel = hiltViewModel(),
    navController: androidx.navigation.NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // State for showing the bottom sheet
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Get UI state from the main view model
    val mainUiState by mainViewModel.uiState.collectAsState()

    // Background color that matches iOS light gray
    val iosBackgroundColor = Color(0xFFF2F2F7)
    val iosBlue = Color(0xFF007AFF)

    // Define colors for each category
    val todayColor = Color(0xFF007AFF) // iOS blue
    val scheduledColor = Color(0xFFFF9500) // iOS orange (changed from red)
    val allColor = Color(0xFF000000) // Black
    val favouriteColor = Color(0xFFFF3B30) // iOS red (changed from orange)
    val completedColor = Color(0xFF8E8E93) // iOS gray

    // Make reminder categories a derived state from the UI state
    // This ensures the counts update when reminders change
    val reminders = uiState.reminders
    val reminderCategories = remember(reminders) {
        listOf(
            ReminderCategoryData(
                type = ReminderType.TODAY,
                title = "Today",
                count = reminders.count { reminder ->
                    (reminder.dueDate?.let { date ->
                        val today = java.util.Calendar.getInstance()
                        val reminderDate = java.util.Calendar.getInstance().apply { time = date }
                        today.get(java.util.Calendar.YEAR) == reminderDate.get(java.util.Calendar.YEAR) &&
                                today.get(java.util.Calendar.DAY_OF_YEAR) == reminderDate.get(java.util.Calendar.DAY_OF_YEAR)
                    } ?: false) && !reminder.isCompleted
                },
                color = todayColor,
                icon = Icons.Default.DateRange
            ),
            ReminderCategoryData(
                type = ReminderType.SCHEDULED,
                title = "Scheduled",
                count = reminders.count { it.dueDate != null && !it.isCompleted },
                color = scheduledColor,
                icon = Icons.Default.DateRange
            ),
            ReminderCategoryData(
                type = ReminderType.ALL,
                title = "All",
                count = reminders.size,
                color = allColor,
                icon = Icons.Default.List
            ),
            ReminderCategoryData(
                type = ReminderType.FAVOURITE,
                title = "Favourite",
                count = reminders.count { it.isFavorite },
                color = favouriteColor,
                icon = Icons.Default.Favorite
            ),
            ReminderCategoryData(
                type = ReminderType.COMPLETED,
                title = "Completed",
                count = reminders.count { it.isCompleted },
                color = completedColor,
                icon = Icons.Default.CheckCircle
            )
        )
    }

    Scaffold(
        containerColor = iosBackgroundColor,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        // Use LazyListState to track scroll position
        val lazyListState = rememberLazyListState()
        
        // Calculate search bar height and convert to pixels
        val searchBarHeight = 68 // Height of search bar + padding in dp
        val searchBarHeightPx = with(LocalDensity.current) { searchBarHeight.dp.toPx() }
        
        // Calculate search bar visibility based on scroll
        val searchBarVisible = remember {
            derivedStateOf {
                // Show the search bar when at or near the top
                // Hide it when scrolling down and away from the top
                val isNearTop = lazyListState.firstVisibleItemIndex == 0 && 
                    lazyListState.firstVisibleItemScrollOffset < 100
                
                isNearTop // Show when near top, hide when scrolling down
            }
        }
        
        // Main content with floating search bar
        Box(modifier = Modifier.fillMaxSize()) {
            // Scrollable content
            // Super elastic, stretchy iOS-like overscroll effect
            val iosOverscrollEffect = ScrollableDefaults.flingBehavior().let { original ->
                object : FlingBehavior {
                    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                        // Dramatically exaggerate the overscroll with a much higher multiplier
                        // This creates an extremely stretchy, rubber-band like effect
                        val stretchFactor = 2.5f
                        val superExaggeratedVelocity = initialVelocity * stretchFactor
                        
                        // Apply additional scrolling before deceleration to create more "stretch"
                        val distanceToAdd = (initialVelocity.absoluteValue * 0.05f).coerceAtMost(50f)
                        if (initialVelocity > 0) {
                            scrollBy(distanceToAdd)
                        } else if (initialVelocity < 0) {
                            scrollBy(-distanceToAdd)
                        }
                        
                        // Use the exaggerated fling behavior for bouncy deceleration
                        return with(original) { 
                            performFling(superExaggeratedVelocity)
                        }
                    }
                }
            }
            
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding() + 40.dp, // Much more space for extreme bounce effect
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                ),
                flingBehavior = iosOverscrollEffect
            ) {
                // No spacer needed at the top
                
                // Main content item (categories grid)
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        // Grid of reminder categories (Today, Scheduled, etc.) using non-lazy Grid
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            // First row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                ReminderCategoryCardAlt(
                                    category = reminderCategories[0],
                                    onClick = { navigateToFilteredList(reminderCategories[0].type) },
                                    modifier = Modifier.weight(1f)
                                )
                                ReminderCategoryCardAlt(
                                    category = reminderCategories[1],
                                    onClick = { navigateToFilteredList(reminderCategories[1].type) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Second row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                ReminderCategoryCardAlt(
                                    category = reminderCategories[2],
                                    onClick = { navigateToFilteredList(reminderCategories[2].type) },
                                    modifier = Modifier.weight(1f)
                                )
                                ReminderCategoryCardAlt(
                                    category = reminderCategories[3],
                                    onClick = { navigateToFilteredList(reminderCategories[3].type) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Third row (with the last category)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                ReminderCategoryCardAlt(
                                    category = reminderCategories[4],
                                    onClick = { navigateToFilteredList(reminderCategories[4].type) },
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.weight(1f)) // Empty space for balance
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Only show "My Lists" section if there are lists available
                        if (mainUiState.lists.isNotEmpty()) {
                            // "My Lists" title like iOS
                            Text(
                                text = "My Lists",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            // Non-scrollable section - list of user's lists
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp) // Add spacing between list items
                            ) {
                                mainUiState.lists.forEach { list ->
                                    val listColor = try {
                                        Color(android.graphics.Color.parseColor(list.color))
                                    } catch (e: Exception) {
                                        Color(0xFF007AFF) // Default iOS blue
                                    }

                                    EnhancedListItem(title = list.name,
                                        count = viewModel.getFilteredReminders()
                                            .count { it.listId == list.id },
                                        icon = Icons.Default.List,
                                        iconBackgroundColor = listColor,
                                        onClick = {
                                            // Navigate to the list view
                                            navController.navigate(
                                                Screen.ReminderListByList.createRoute(
                                                    list.id,
                                                    list.name,
                                                    list.color
                                                )
                                            )
                                        })
                                }

                                // Add some bottom padding
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }

                // Add spacer at bottom to ensure scrolling content doesn't hide behind floating buttons
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
            
            // Floating buttons that stay in place regardless of scrolling
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(iosBackgroundColor)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // New Reminder button with iOS-style
                    Card(
                        shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        ), elevation = CardDefaults.cardElevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    showBottomSheet = true
                                    coroutineScope.launch { sheetState.show() }
                                }
                                .padding(end = 8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(iosBlue), contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
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
                    var showAddListDialog by remember { mutableStateOf(false) }

                    Text(text = "Add List",
                        color = iosBlue,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clickable { showAddListDialog = true }
                            .padding(8.dp) // Add some padding for the touch target
                    )

                    // Add list dialog
                    if (showAddListDialog) {
                        AddListDialog(onDismiss = { showAddListDialog = false },
                            onAddList = { name ->
                                mainViewModel.addList(name)
                                showAddListDialog = false
                            })
                    }
                }
            }
        }
    }

    // Create a reference to the AddReminderViewModel
    val addReminderViewModel: AddReminderViewModel = hiltViewModel()

    // Bottom sheet for adding a new reminder
    if (showBottomSheet) {
        // Reset the state when showing the bottom sheet
        LaunchedEffect(showBottomSheet) {
            addReminderViewModel.resetState()
        }

        ModalBottomSheet(onDismissRequest = {
            coroutineScope.launch {
                sheetState.hide()
                showBottomSheet = false
                // Also reset state when dismissing
                addReminderViewModel.resetState()
            }
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
                            // Also reset state when navigating back
                            addReminderViewModel.resetState()
                        }
                    }, viewModel = addReminderViewModel // Pass the ViewModel instance
                )
            }
        }
    }
}

@Composable
private fun AddListDialog(
    onDismiss: () -> Unit, onAddList: (String) -> Unit
) {
    var listName by remember { mutableStateOf("") }

    // Reference to control focus and automatically show keyboard
    val focusRequester = FocusRequester()

    // Effect to request focus when dialog is shown
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100) // Short delay to ensure the dialog is fully shown
        focusRequester.requestFocus()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp), color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "New List",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = listName,
                    onValueChange = { listName = it },
                    label = { Text("List Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    TextButton(
                        onClick = { onAddList(listName) }, enabled = listName.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}