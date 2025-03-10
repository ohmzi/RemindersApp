package com.ohmz.remindersapp.ui.reminderScreen.reminderScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun TitleNotesCard(
    title: String,
    onTitleChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // iOS-like light background color
    val iOSBackgroundColor = Color(0xFFFFFFFF) // Pure white background
    // iOS-like divider color
    val iOSDividerColor = Color(0xFFE5E5EA) // Lighter divider color
    // Text color for iOS
    val iOSTextColor = Color(0xFF000000)
    // iOS-like placeholder color
    val iOSPlaceholderColor = Color(0xFFC7C7CC)

    // Create a focus requester
    val focusRequester = remember { FocusRequester() }

    // Request focus when the composable enters the composition
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = iOSBackgroundColor),
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Title text field with focus
            TextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = { Text("Title", color = iOSPlaceholderColor) },
                // Remove default background/outline
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = iOSBackgroundColor,
                    unfocusedContainerColor = iOSBackgroundColor,
                    focusedTextColor = iOSTextColor,
                    unfocusedTextColor = iOSTextColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .focusRequester(focusRequester), // Apply the focus requester
                maxLines = 1,
                singleLine = true
            )

            // Thin divider
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = iOSDividerColor,
                thickness = 0.5.dp
            )

            // Notes text field with increased height
            TextField(
                value = notes,
                onValueChange = onNotesChange,
                placeholder = { Text("Notes", color = iOSPlaceholderColor) },
                // Remove default background/outline
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = iOSBackgroundColor,
                    unfocusedContainerColor = iOSBackgroundColor,
                    focusedTextColor = iOSTextColor,
                    unfocusedTextColor = iOSTextColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Increase the height here
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                maxLines = 10  // Increase max lines
            )
        }
    }
}