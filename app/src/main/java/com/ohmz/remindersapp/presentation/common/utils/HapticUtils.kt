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
    // Try several types of haptic feedback to ensure one works
    try {
        // Try stronger feedback first
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    } catch (e: Exception) {
        try {
            // Fallback to a different type
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        } catch (e: Exception) {
            try {
                // Last resort - using a different type that is available
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            } catch (e: Exception) {
                // Silently fail if haptic is not supported
            }
        }
    }
}

/**
 * Perform a standard click haptic feedback
 * @param hapticFeedback The HapticFeedback instance to use
 */
fun performClickHaptic(hapticFeedback: HapticFeedback) {
    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
}

/**
 * Perform a success haptic feedback
 * @param hapticFeedback The HapticFeedback instance to use
 */
fun performSuccessHaptic(hapticFeedback: HapticFeedback) {
    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
}