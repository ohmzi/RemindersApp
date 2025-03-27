package com.ohmz.remindersapp.presentation.common.utils

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * Utility functions for providing standardized haptic feedback across the app
 */

/**
 * Perform an enhanced delete haptic feedback
 * @param hapticFeedback The HapticFeedback instance to use
 */
fun performDeleteHaptic(hapticFeedback: HapticFeedback) {
    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
}

/**
 * Perform a standard click haptic feedback
 * @param hapticFeedback The HapticFeedback instance to use
 */
fun performClickHaptic(hapticFeedback: HapticFeedback) {
    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
}