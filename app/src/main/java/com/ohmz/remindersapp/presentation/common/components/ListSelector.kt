package com.ohmz.remindersapp.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ohmz.remindersapp.domain.model.ReminderList
import com.ohmz.remindersapp.presentation.common.theme.AppTheme
import com.ohmz.remindersapp.presentation.common.theme.IOSColors

/**
 * A component that displays list selection options similar to the PrioritySelector
 */
@Composable
fun ListSelector(
    lists: List<ReminderList>,
    selectedListId: Int?,
    onListSelected: (ReminderList) -> Unit,
    onAddNewList: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddListDialog by remember { mutableStateOf(false) }
    val iosBlue = IOSColors.Blue
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // First row for existing lists (scrollable horizontally if many)
        if (lists.isNotEmpty()) {
            // Create a scrollState we can control programmatically
            val scrollState = rememberScrollState()
            
            // Find the selected list button and its estimated position
            // We'll approximate based on average button width - actual implementation may need adjustments
            val selectedListButtonWidth = 120.dp  // estimated average width with padding
            val initialPadding = 20.dp
            
            // Find the index of the selected list for precise scrolling
            val selectedIndex = lists.indexOfFirst { it.id == selectedListId }
            
            // Use LaunchedEffect to scroll to the selected item when shown
            // This prevents the list from jumping around when you re-open it
            androidx.compose.runtime.LaunchedEffect(selectedListId) {
                if (selectedIndex >= 0) {
                    // Calculate approximate scroll position based on button width
                    val scrollPosition = (initialPadding.value + (selectedIndex * selectedListButtonWidth.value)).toInt()
                    
                    // Don't animate the scroll - just set position directly
                    // This prevents visible movement when opening
                    scrollState.scrollTo(scrollPosition)
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(state = scrollState),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Add some initial padding
                Spacer(modifier = Modifier.width(4.dp))
                
                // Keep the lists in their original sorted order (alphabetical)
                lists.forEach { list ->
                    ListButton(
                        text = list.name,
                        isSelected = list.id == selectedListId,
                        activeColor = list.color,
                        onClick = { onListSelected(list) }
                    )
                }
                
                // Add some final padding
                Spacer(modifier = Modifier.width(4.dp))
            }
            
            // Add some space between rows
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            // Show a message when there are no lists
            Text(
                text = "No lists yet. Create your first list below.",
                color = IOSColors.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
            
            // Add some space between message and button
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Second row - Add new list button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            // Define colors based on state (dialog showing or not)
            val primaryColor = IOSColors.Blue  // Primary from IOSLightColorScheme
            val grayColor = AppTheme.secondaryBackground
            
            // Colors change based on whether dialog is showing
            val backgroundColor = if (showAddListDialog) primaryColor.copy(alpha = 0.15f) else grayColor
            val borderColor = if (showAddListDialog) primaryColor else IOSColors.ButtonGrayBorder
            val textColor = if (showAddListDialog) primaryColor else AppTheme.primaryText
            
            // Create a custom button that matches the ListButton style
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(backgroundColor)
                    .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                    .clickable { showAddListDialog = true }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add new list",
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "New List",
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
    
    // Dialog to add a new list
    if (showAddListDialog) {
        AddListDialog(
            onDismiss = { showAddListDialog = false },
            onAddList = { 
                onAddNewList(it)
                showAddListDialog = false
            }
        )
    }
}

@Composable
private fun ListButton(
    text: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Define colors
    val iosBlue = IOSColors.Blue
    val grayColor = AppTheme.secondaryBackground
    val grayBorder = IOSColors.ButtonGrayBorder
    
    val backgroundColor = if (isSelected) activeColor.copy(alpha = 0.15f) else grayColor
    val borderColor = if (isSelected) activeColor else grayBorder
    val textColor = if (isSelected) activeColor else AppTheme.primaryText
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Show list icon
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = text,
                color = textColor,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun AddListDialog(
    onDismiss: () -> Unit,
    onAddList: (String) -> Unit
) {
    var listName by remember { mutableStateOf("") }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AppTheme.cardBackground
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
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    TextButton(
                        onClick = { onAddList(listName) },
                        enabled = listName.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}