package com.ohmz.remindersapp.presentation.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A reusable Android-style top app bar that mimics the Google Contacts style
 * with proper positioning of the back button and no centered title
 */
@Composable
fun AndroidStyleTopBar(
    onBackClick: () -> Unit,
    showAddButton: Boolean = false,
    onAddClick: () -> Unit = {},
    iconTint: Color = Color(0xFF007AFF), // Default iOS blue
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp) // Standard Android app bar height
    ) {
        // Back button with Google Contacts positioning
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = iconTint,
            modifier = Modifier
                .padding(start = 16.dp, top = 10.dp)
                .size(26.dp)
                .align(Alignment.CenterStart)
                .clickable(onClick = onBackClick)
        )

        // No centered title text

        // Add button if requested
        if (showAddButton) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = iconTint,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(28.dp)
                    .clickable(onClick = onAddClick)
            )
        }
    }
}