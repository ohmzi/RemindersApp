package com.ohmz.remindersapp.presentation.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
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
import com.ohmz.remindersapp.presentation.common.theme.IOSColors

@Composable
fun TitleNotesCard(
    title: String,
    onTitleChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Get colors from the theme
    val appColors = com.ohmz.remindersapp.presentation.common.theme.AppTheme
    
    // Card background color
    val cardBackgroundColor = appColors.cardBackground
    val textColour = appColors.primaryText
    // Divider color
    val dividerColor = if (appColors.isDark) 
        com.ohmz.remindersapp.presentation.common.theme.AppColors.Gray.GrayDark4 
    else 
        com.ohmz.remindersapp.presentation.common.theme.AppColors.Gray.Gray5
    // Placeholder color
    val placeholderColor = if (appColors.isDark) 
        com.ohmz.remindersapp.presentation.common.theme.AppColors.Gray.GrayDark3 
    else 
        com.ohmz.remindersapp.presentation.common.theme.AppColors.Gray.Gray3

    // Create a focus requester
    val focusRequester = remember { FocusRequester() }

    // Request focus when the composable enters the composition
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Title text field with focus
            TextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = { Text("Title", color = textColour) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = cardBackgroundColor,
                    unfocusedContainerColor = cardBackgroundColor,
                    focusedIndicatorColor = IOSColors.Transparent,
                    unfocusedIndicatorColor = IOSColors.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .focusRequester(focusRequester),
                maxLines = 1,
                singleLine = true
            )

            // Thin divider
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = dividerColor,
                thickness = 0.5.dp
            )

            // Notes text field with increased height
            TextField(
                value = notes,
                onValueChange = onNotesChange,
                placeholder = { Text("Notes", color = textColour) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = cardBackgroundColor,
                    unfocusedContainerColor = cardBackgroundColor,
                    focusedIndicatorColor = IOSColors.Transparent,
                    unfocusedIndicatorColor = IOSColors.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                maxLines = 10
            )
        }
    }
}
