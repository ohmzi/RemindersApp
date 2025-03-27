package com.ohmz.remindersapp.presentation.common.utils

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Helper function to handle bottom sheet dismissal with confirmation dialog when needed
 *
 * @param hasChanges Whether there are unsaved changes
 * @param showDiscardDialog Function to show the discard confirmation dialog
 * @param dismiss Function to dismiss the bottom sheet
 */
fun handleBottomSheetDismiss(
    hasChanges: Boolean,
    showDiscardDialog: () -> Unit,
    dismiss: () -> Unit
) {
    if (hasChanges) {
        // Show confirmation dialog if there are unsaved changes
        showDiscardDialog()
    } else {
        // No changes, just dismiss
        dismiss()
    }
}

/**
 * Helper function to dismiss a bottom sheet and reset state
 *
 * @param coroutineScope Coroutine scope to launch the dismissal operation
 * @param sheetState The sheet state to hide
 * @param hideBottomSheet Function to update the showBottomSheet state
 * @param resetState Function to reset the view model state
 */
@OptIn(ExperimentalMaterial3Api::class)
fun dismissBottomSheet(
    coroutineScope: CoroutineScope,
    sheetState: SheetState,
    hideBottomSheet: () -> Unit,
    resetState: () -> Unit
) {
    coroutineScope.launch {
        sheetState.hide()
        resetState()
    }
    hideBottomSheet()
}

/**
 * Enhanced Floating Action Button with improved animation and visual feedback
 *
 * @param onClick Action to perform when the button is clicked
 * @param containerColor Color of the FAB
 * @param contentColor Color of the icon
 * @param expandedWidth Whether to double the width of the FAB
 * @param content Content of the FAB (usually an Icon)
 */
@Composable
fun EnhancedFAB(
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Use interaction source to track press state
    val interactionSource = remember { MutableInteractionSource() }

    // Track if button is pressed
    val isPressed by interactionSource.collectIsPressedAsState()

    // Create darker version of the container color (70% brightness)
    val pressedColor = containerColor.copy(
        red = containerColor.red * 0.7f,
        green = containerColor.green * 0.7f,
        blue = containerColor.blue * 0.7f
    )

    // Use the darker color when pressed
    val currentColor = if (isPressed) pressedColor else containerColor

    
    FloatingActionButton(
        onClick = onClick,
        containerColor = currentColor,
        contentColor = contentColor,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 8.dp,          // Higher default elevation for more depth
            pressedElevation = 2.dp,          // Much lower when pressed for a satisfying press effect
            focusedElevation = 6.dp,
            hoveredElevation = 10.dp          // Higher on hover for interactive feel
        ),
        interactionSource = interactionSource,
        modifier = modifier
            .width(100.dp)
            .height((60.dp)) // Double the width of a standard FAB
    ) {
        content()
    }
}