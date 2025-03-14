package com.ohmz.remindersapp.presentation.reminder.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.ohmz.remindersapp.domain.model.ReminderType
import com.ohmz.remindersapp.presentation.common.components.EnhancedListItem
import com.ohmz.remindersapp.presentation.common.components.ReminderCategoryCardAlt
import com.ohmz.remindersapp.presentation.common.components.ReminderCategoryData
import com.ohmz.remindersapp.presentation.common.theme.AppTheme
import com.ohmz.remindersapp.presentation.common.theme.IOSColors
import com.ohmz.remindersapp.presentation.navigation.Screen
import com.ohmz.remindersapp.presentation.reminder.add.AddReminderScreen
import com.ohmz.remindersapp.presentation.reminder.add.AddReminderViewModel
import com.ohmz.remindersapp.presentation.reminder.list.ReminderListViewModel
import kotlinx.coroutines.launch
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

    // Get UI state from the main view model
    val mainUiState by mainViewModel.uiState.collectAsState()

    // Create a reference to the AddReminderViewModel first, before it's used
    val addReminderViewModel: AddReminderViewModel = hiltViewModel()

    // State for showing the bottom sheet
    var showBottomSheet by remember { mutableStateOf(false) }
    // State for tracking if we should show the discard dialog
    var showDiscardBottomSheetDialog by remember { mutableStateOf(false) }

    // Now we can use addReminderViewModel safely
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // Use theme colors from AppTheme
    val appColors = AppTheme

    // Background color from theme
    val backgroundColors = appColors.mainBackground

    // Category colors from theme
    val todayColor = appColors.todayColor
    val scheduledColor = appColors.scheduledColor
    val allColor = appColors.allColor
    val favouriteColor = appColors.favoriteColor
    val completedColor = appColors.completedColor

    // Use the cached counts from the UI state to prevent initial zeros
    // This ensures we show meaningful numbers immediately upon app start
    val counts = uiState.categoryCounts
    val reminderCategories = listOf(
        ReminderCategoryData(
            type = ReminderType.TODAY,
            title = "Today",
            count = counts.todayCount,
            color = todayColor,
            icon = Icons.Default.DateRange
        ),
        ReminderCategoryData(
            type = ReminderType.SCHEDULED,
            title = "Scheduled",
            count = counts.scheduledCount,
            color = scheduledColor,
            icon = Icons.Default.DateRange
        ),
        ReminderCategoryData(
            type = ReminderType.ALL,
            title = "All",
            count = counts.allCount,
            color = allColor,
            icon = Icons.Default.List
        ),
        ReminderCategoryData(
            type = ReminderType.FAVOURITE,
            title = "Favourite",
            count = counts.favoriteCount,
            color = favouriteColor,
            icon = Icons.Default.Favorite
        ),
        ReminderCategoryData(
            type = ReminderType.COMPLETED,
            title = "Completed",
            count = counts.completedCount,
            color = completedColor,
            icon = Icons.Default.CheckCircle
        )
    )

    // State for showing the add list dialog
    var showAddListDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = backgroundColors,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Reset state for new reminder
                    addReminderViewModel.resetState()
                    showBottomSheet = true
                    coroutineScope.launch { sheetState.show() }
                },
                containerColor = appColors.todayColor,
                contentColor = IOSColors.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Reminder"
                )
            }
        },
        // Custom content to add an additional FAB on the left side
        floatingActionButtonPosition = FabPosition.End
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
                        val distanceToAdd =
                            (initialVelocity.absoluteValue * 0.05f).coerceAtMost(50f)
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

                        // Regular spacer between categories and "My Lists"
                        Spacer(modifier = Modifier.height(24.dp))

                        // Only show "My Lists" section if there are lists available
// Updated section for ReminderMainScreen.kt that handles list management
// This replaces the "My Lists" section in the original code

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

                                    EnhancedListItem(
                                        title = list.name,
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
                                        },
                                        list = list, // Pass the list object
                                        onRenameList = { reminderList, newName ->
                                            // Call ViewModel function to rename the list
                                            mainViewModel.renameList(reminderList, newName)
                                        },
                                        onChangeListColor = { reminderList, newColor ->
                                            // Call ViewModel function to change list color
                                            mainViewModel.updateListColor(reminderList, newColor)
                                        },
                                        onDeleteList = { reminderList ->
                                            // Call ViewModel function to delete the list
                                            mainViewModel.deleteList(reminderList)
                                        }
                                    )
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

            // Add list dialog - shown when the Add List FAB is clicked
            if (showAddListDialog) {
                AddListDialog(
                    onDismiss = { showAddListDialog = false },
                    onAddList = { name ->
                        mainViewModel.addList(name)
                        showAddListDialog = false
                    }
                )
            }

            // Add List FAB positioned on the left side
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 16.dp)
                    .navigationBarsPadding()
            ) {
                FloatingActionButton(
                    onClick = {
                        showAddListDialog = true
                    },
                    containerColor = IOSColors.Red, // Red color for contrast with the blue Add Reminder FAB
                    contentColor = IOSColors.White
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Add List"
                    )
                }
            }
        }
    }

    // Bottom sheet for adding a new reminder
    if (showBottomSheet) {
        // Dialog for discarding changes when dismissing the bottom sheet
        if (showDiscardBottomSheetDialog) {
            Dialog(onDismissRequest = { showDiscardBottomSheetDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = IOSColors.White
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
                            onClick = {
                                showDiscardBottomSheetDialog = false
                                coroutineScope.launch {
                                    addReminderViewModel.resetState()
                                    sheetState.hide()
                                    showBottomSheet = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Discard Changes",
                                color = IOSColors.Red,
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp
                            )
                        }

                        // Cancel Button (Blue)
                        TextButton(
                            onClick = {
                                showDiscardBottomSheetDialog = false
                                // Important: Force the sheet to be shown again
                                coroutineScope.launch {
                                    try {
                                        // Make sure the sheet is shown again
                                        sheetState.show()
                                    } catch (e: Exception) {
                                        // Handle any potential exceptions
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Cancel",
                                color = appColors.todayColor,
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // Reset the state when showing the bottom sheet initially
        LaunchedEffect(showBottomSheet) {
            addReminderViewModel.resetState()
        }

        ModalBottomSheet(
            onDismissRequest = {
                // This is called when user taps outside or presses back
                // We'll always check for changes here
                handleBottomSheetDismiss(
                    hasChanges = addReminderViewModel.hasUnsavedChanges(),
                    showDialog = { showDiscardBottomSheetDialog = true },
                    dismiss = {
                        coroutineScope.launch {
                            sheetState.hide()
                            showBottomSheet = false
                            addReminderViewModel.resetState()
                        }
                    }
                )
            },
            sheetState = sheetState,
            dragHandle = { } // Hide the drag handle to make it less obvious it can be dragged
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
            ) {
                AddReminderScreen(
                    onNavigateBack = {
                        // This is called from the "Cancel" button in AddReminderScreen
                        // AddReminderScreen already handles showing confirmation dialog if needed
                        // We just need to dismiss the sheet here
                        coroutineScope.launch {
                            sheetState.hide()
                            showBottomSheet = false
                        }
                    }, viewModel = addReminderViewModel // Pass the ViewModel instance
                )
            }
        }
    }
}

/**
 * Helper function to handle bottom sheet dismissal with confirmation when needed
 */
private fun handleBottomSheetDismiss(
    hasChanges: Boolean,
    showDialog: () -> Unit,
    dismiss: () -> Unit
) {
    if (hasChanges) {
        // Show confirmation dialog
        showDialog()
    } else {
        // No changes, just dismiss
        dismiss()
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
            shape = RoundedCornerShape(16.dp), color = IOSColors.White
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