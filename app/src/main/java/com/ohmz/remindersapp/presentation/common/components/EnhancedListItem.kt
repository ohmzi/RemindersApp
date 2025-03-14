package com.ohmz.remindersapp.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ohmz.remindersapp.domain.model.ReminderList
import com.ohmz.remindersapp.presentation.common.theme.IOSColors

/**
 * Enhanced iOS-style list item with consistent rounded corners and shadow
 * Now includes options dialog when clicking on the icon
 */
@Composable
fun EnhancedListItem(
    title: String,
    count: Int,
    icon: ImageVector,
    iconBackgroundColor: Color,
    onClick: () -> Unit,
    list: ReminderList? = null,  // Optional ReminderList object
    onRenameList: ((ReminderList, String) -> Unit)? = null, // Callback for renaming
    onChangeListColor: ((ReminderList, Color) -> Unit)? = null, // Callback for changing color
    onDeleteList: ((ReminderList) -> Unit)? = null // Callback for deleting
) {
    var showOptionsDialog by remember { mutableStateOf(false) }

    val cornerShape = RoundedCornerShape(12.dp)

    Card(
        modifier = Modifier.fillMaxWidth(),
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with colored background - Now clickable to show options
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor)
                    .clickable(
                        enabled = list != null &&
                                onRenameList != null &&
                                onChangeListColor != null &&
                                onDeleteList != null,
                        onClick = { showOptionsDialog = true }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = IOSColors.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Increased spacing between icon and text
            Spacer(modifier = Modifier.width(24.dp))

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            // Item count
            Text(
                text = count.toString(),
                fontSize = 18.sp,
                color = IOSColors.Gray
            )
        }
    }

    // Show options dialog if the icon was clicked and we have a valid list object and callbacks
    if (showOptionsDialog && list != null &&
        onRenameList != null && onChangeListColor != null && onDeleteList != null) {
        ListOptionsDialog(
            list = list,
            onDismiss = { showOptionsDialog = false },
            onRename = { newName -> onRenameList(list, newName) },
            onColorChange = { newColor -> onChangeListColor(list, newColor) },
            onDelete = { onDeleteList(list) }
        )
    }
}