package com.ohmz.remindersapp.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

/**
 * A component that displays date selection options similar to iOS Reminders app
 */
@Composable
fun DateSelector(
    onTodaySelected: () -> Unit,
    onTomorrowSelected: () -> Unit,
    onNextWeekendSelected: () -> Unit,
    onDateTimeSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val calendar = Calendar.getInstance()
    val today = calendar.get(Calendar.DAY_OF_MONTH)

    // Calculate tomorrow
    val tomorrowCalendar = Calendar.getInstance()
    tomorrowCalendar.add(Calendar.DAY_OF_YEAR, 1)
    val tomorrow = tomorrowCalendar.get(Calendar.DAY_OF_MONTH)

    // Calculate next weekend (Saturday)
    val weekendCalendar = Calendar.getInstance()
    val dayOfWeek = weekendCalendar.get(Calendar.DAY_OF_WEEK)
    val daysUntilSaturday = if (dayOfWeek <= Calendar.SATURDAY) {
        Calendar.SATURDAY - dayOfWeek
    } else {
        7 - (dayOfWeek - Calendar.SATURDAY)
    }
    weekendCalendar.add(Calendar.DAY_OF_YEAR, daysUntilSaturday)
    val weekend = weekendCalendar.get(Calendar.DAY_OF_MONTH)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Today date button
        DateButton(
            date = today.toString(),
            label = "Today",
            backgroundColor = Color(0xFF007AFF).copy(alpha = 0.1f),
            borderColor = Color(0xFF007AFF),
            textColor = Color.Black,
            onClick = onTodaySelected
        )

        // Tomorrow date button
        DateButton(
            date = tomorrow.toString(),
            label = "Tomorrow",
            backgroundColor = Color(0xFF007AFF).copy(alpha = 0.1f),
            borderColor = Color(0xFF007AFF),
            textColor = Color.Black,
            onClick = onTomorrowSelected
        )

        // Next Weekend date button
        DateButton(
            date = weekend.toString(),
            label = "Next Weekend",
            backgroundColor = Color(0xFF007AFF).copy(alpha = 0.1f),
            borderColor = Color(0xFF007AFF),
            textColor = Color.Black,
            onClick = onNextWeekendSelected
        )

        // Date & Time button
        DateButton(
            date = "...",
            label = "Date & Time",
            backgroundColor = Color(0xFF007AFF).copy(alpha = 0.1f),
            borderColor = Color(0xFF007AFF),
            textColor = Color.Black,
            onClick = onDateTimeSelected
        )
    }
}

@Composable
fun DateButton(
    date: String,
    label: String,
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(80.dp)
    ) {
        // Date bubble
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp, 40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(backgroundColor)
                .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                .clickable(onClick = onClick)
        ) {
            Text(
                text = date,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
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

/**
 * A component that displays the action buttons below the date selector
 */
@Composable
fun DateActionBar(
    isCalendarSelected: Boolean,
    isLocationSelected: Boolean,
    isTagSelected: Boolean,
    isFavoriteSelected: Boolean,
    isPersonSelected: Boolean,
    onCalendarClick: () -> Unit,
    onLocationClick: () -> Unit,
    onTagClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onPersonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionIcon(
            icon = Icons.Default.DateRange,
            contentDescription = "Calendar",
            isSelected = isCalendarSelected,
            selectedColor = Color(0xFF0000FF), // Blue for selected
            unselectedColor = Color.Gray,
            onClick = onCalendarClick
        )

        // Add the location, tag, favorite, and person icons similarly
        // You'll need to import appropriate icons for each
    }
}

@Composable
fun ActionIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = if (isSelected) selectedColor else unselectedColor,
        modifier = modifier
            .size(28.dp)
            .clickable(onClick = onClick)
    )
}