package com.ohmz.remindersapp.presentation.common.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ohmz.remindersapp.domain.model.ReminderType
import com.ohmz.remindersapp.presentation.common.theme.IOSColors
import kotlin.math.roundToInt

/**
 * Enhanced data class for reminder card information with iOS styling
 */
data class ReminderCategoryData(
    val type: ReminderType,
    val title: String,
    val count: Int,
    val icon: ImageVector,
    val color: Color = IOSColors.Blue
)

/**
 * iOS-style category card with consistent rounded corners and shadow
 */
@Composable
fun ReminderCategoryCardAlt(
    category: ReminderCategoryData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Define the corner shape once to keep it consistent
    val cornerShape = RoundedCornerShape(12.dp)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = cornerShape,
        colors = CardDefaults.cardColors(containerColor = IOSColors.White),
        // Subtle iOS-style shadow that respects the corner shape
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 0.5.dp, // Subtle pressed effect
            focusedElevation = 1.dp,
            hoveredElevation = 1.5.dp
        ),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Category icon in a colored circle (larger and more prominent)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(category.color)
                    .align(Alignment.TopStart),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.title,
                    tint = IOSColors.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Category title moved farther down for better spacing
            Text(
                text = category.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 2.dp, top = 42.dp)
            )

            // Count number
            Text(
                text = category.count.toString(),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}