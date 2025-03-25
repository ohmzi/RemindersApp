package com.ohmz.remindersapp.presentation.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ohmz.remindersapp.presentation.common.theme.AppTheme
import com.ohmz.remindersapp.presentation.common.theme.IOSColors

/**
 * A reusable dialog component for confirmation when discarding unsaved changes
 *
 * @param onDismiss Called when the dialog is dismissed without taking action
 * @param onDiscard Called when user confirms discarding changes
 * @param onContinueEditing Called when user chooses to continue editing
 */
@Composable
fun DiscardDialog(
    onDismiss: () -> Unit,
    onDiscard: () -> Unit,
    onContinueEditing: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AppTheme.cardBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Discard Changes?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "You have unsaved changes that will be lost if you discard this reminder.",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                // Discard Changes Button (Red)
                OutlinedButton(
                    onClick = onDiscard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    border = BorderStroke(1.dp, IOSColors.Red),
                    colors = ButtonDefaults.outlinedButtonColors(
                        // In older versions, we can only set content color
                        contentColor = IOSColors.Red
                    )
                ) {
                    Text(
                        text = "Discard Changes",
                        color = IOSColors.Red,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                }

                // Continue Editing Button (Blue)
                OutlinedButton(
                    onClick = onContinueEditing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    border = BorderStroke(1.dp, AppTheme.todayColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        // In older versions, we can only set content color
                        contentColor = AppTheme.todayColor
                    )
                ) {
                    Text(
                        text = "Continue Editing",
                        color = AppTheme.todayColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}