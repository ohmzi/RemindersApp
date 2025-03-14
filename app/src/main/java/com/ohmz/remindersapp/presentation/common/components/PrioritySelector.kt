package com.ohmz.remindersapp.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.ohmz.remindersapp.presentation.common.theme.IOSColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ohmz.remindersapp.domain.model.Priority

/**
 * A component that displays priority selection options similar to iOS Reminders app
 */
@Composable
fun PrioritySelector(
    onLowPrioritySelected: () -> Unit,
    onMediumPrioritySelected: () -> Unit,
    onHighPrioritySelected: () -> Unit,
    modifier: Modifier = Modifier,
    currentPriority: Priority = Priority.MEDIUM
) {
    // Remember which option is currently selected
    val selectedOption = remember { mutableStateOf(currentPriority.name) }

    // Define colors
    val lowPriorityColor = IOSColors.Green
    val mediumPriorityColor = IOSColors.Blue
    val highPriorityColor = IOSColors.Red
    val grayColor = IOSColors.ButtonGray
    val grayBorder = IOSColors.ButtonGrayBorder

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Low Priority button
        PriorityButton(
            text = "Low",
            label = "",
            isSelected = selectedOption.value == Priority.LOW.name,
            activeColor = lowPriorityColor,
            onClick = {
                selectedOption.value = Priority.LOW.name
                onLowPrioritySelected()
            }
        )

        // Medium Priority button
        PriorityButton(
            text = "Medium",
            label = "",
            isSelected = selectedOption.value == Priority.MEDIUM.name,
            activeColor = mediumPriorityColor,
            onClick = {
                selectedOption.value = Priority.MEDIUM.name
                onMediumPrioritySelected()
            }
        )

        // High Priority button
        PriorityButton(
            text = "High",
            label = "",
            isSelected = selectedOption.value == Priority.HIGH.name,
            activeColor = highPriorityColor,
            onClick = {
                selectedOption.value = Priority.HIGH.name
                onHighPrioritySelected()
            }
        )
    }
}

@Composable
fun PriorityButton(
    text: String,
    label: String,
    isSelected: Boolean,
    activeColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Define colors for the button
    val backgroundColor = if (isSelected) activeColor.copy(alpha = 0.15f) else IOSColors.ButtonGray
    val borderColor = if (isSelected) activeColor else IOSColors.ButtonGrayBorder
    val textColor = if (isSelected) activeColor else IOSColors.DarkGrayText

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(100.dp)
    ) {
        // Priority bubble
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(90.dp, 40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(backgroundColor)
                .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                .clickable(onClick = onClick)
        ) {
            Text(
                text = text,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }

        // Label
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}