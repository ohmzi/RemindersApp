package com.ohmz.remindersapp.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ohmz.remindersapp.domain.model.ReminderList
import com.ohmz.remindersapp.presentation.common.theme.IOSColors

/**
 * Dialog that appears when a user taps on a list icon
 * Allows renaming, color changing, and deleting a list
 */
@Composable
fun ListOptionsDialog(
    list: ReminderList,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit,
    onColorChange: (Color) -> Unit,
    onDelete: () -> Unit
) {
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showColorPickerDialog by remember { mutableStateOf(false) }

    // Main options dialog
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp), 
            color = IOSColors.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Title with list name
                Text(
                    text = list.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Options

                // Rename option
                ListOptionItem(
                    icon = Icons.Default.Edit,
                    text = "Rename list",
                    onClick = { showRenameDialog = true }
                )

                // Change color option
                ListOptionItem(
                    icon = Icons.Default.Build,
                    text = "Change color",
                    onClick = { showColorPickerDialog = true }
                )

                // Delete option
                ListOptionItem(
                    icon = Icons.Default.Delete,
                    text = "Delete list",
                    textColor = IOSColors.Red,
                    onClick = { showConfirmDeleteDialog = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cancel button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IOSColors.Gray5,
                        contentColor = IOSColors.Black
                    )
                ) {
                    Text("Cancel")
                }
            }
        }
    }

    // Rename dialog
    if (showRenameDialog) {
        RenameListDialog(currentName = list.name,
            onDismiss = { showRenameDialog = false },
            onRename = { newName ->
                onRename(newName)
                showRenameDialog = false
                onDismiss()
            })
    }

    // Color picker dialog
    if (showColorPickerDialog) {
        ColorPickerDialog(
            currentColor = list.color,
            onDismiss = { showColorPickerDialog = false },
            onColorSelected = { selectedColor ->
                onColorChange(selectedColor)
                showColorPickerDialog = false
                onDismiss()
            }
        )
    }

    // Confirm delete dialog
    if (showConfirmDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDeleteDialog = false },
            title = { Text("Delete List") },
            text = { Text("Are you sure you want to delete the \"${list.name}\" list? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showConfirmDeleteDialog = false
                    onDismiss()
                }) {
                    Text("Delete", color = IOSColors.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ListOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    textColor: Color = IOSColors.Black,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon, 
            contentDescription = text, 
            tint = textColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text, 
            color = textColor, 
            fontSize = 16.sp
        )
    }
}

@Composable
private fun RenameListDialog(
    currentName: String, onDismiss: () -> Unit, onRename: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100) // Short delay to ensure dialog is shown
        focusRequester.requestFocus()
    }

    Dialog(onDismissRequest = onDismiss) {
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
                    text = "Rename List",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("List Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    TextButton(
                        onClick = { onRename(newName) },
                        enabled = newName.isNotBlank() && newName != currentName
                    ) {
                        Text("Save", color = IOSColors.Blue)
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorPickerDialog(
    currentColor: Color, onDismiss: () -> Unit, onColorSelected: (Color) -> Unit
) {
    // Use colors directly from the IOSColors object
    val colorOptions = listOf(
        IOSColors.Blue,       // iOS Blue
        IOSColors.Red,        // iOS Red
        IOSColors.Orange,     // iOS Orange
        IOSColors.Yellow,     // iOS Yellow
        IOSColors.Green,      // iOS Green
        IOSColors.Gray        // iOS Gray
    )

    var selectedColor by remember { mutableStateOf(currentColor) }

    Dialog(onDismissRequest = onDismiss) {
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
                    text = "Choose Color",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(colorOptions) { colorValue ->
                        ColorOption(
                            color = colorValue,
                            isSelected = colorValue == selectedColor,
                            onClick = { selectedColor = colorValue }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    TextButton(
                        onClick = { onColorSelected(selectedColor) },
                        enabled = selectedColor != currentColor
                    ) {
                        Text("Apply", color = IOSColors.Blue)
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorOption(
    color: Color, isSelected: Boolean, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 0.dp, 
                color = IOSColors.Black, 
                shape = CircleShape
            )
            .clickable(onClick = onClick), 
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(IOSColors.White)
            )
        }
    }
}