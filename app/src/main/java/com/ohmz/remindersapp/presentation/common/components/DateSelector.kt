package com.ohmz.remindersapp.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.ohmz.remindersapp.presentation.common.theme.IOSColors
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import java.util.Date

/**
 * A component that displays date selection options similar to iOS Reminders app
 */
@Composable
fun DateSelector(
    onTodaySelected: () -> Unit,
    onTomorrowSelected: () -> Unit,
    onNextWeekendSelected: () -> Unit,
    onDateTimeSelected: () -> Unit,
    modifier: Modifier = Modifier,
    currentDate: Date? = null
) {
    // Determine which option should be initially selected based on the current date
    val initialSelection = if (currentDate != null) {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val tomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val weekend = Calendar.getInstance().apply {
            val dayOfWeek = get(Calendar.DAY_OF_WEEK)
            val daysUntilSaturday = if (dayOfWeek <= Calendar.SATURDAY) {
                Calendar.SATURDAY - dayOfWeek
            } else {
                7 - (dayOfWeek - Calendar.SATURDAY)
            }
            add(Calendar.DAY_OF_YEAR, daysUntilSaturday)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val currentCal = Calendar.getInstance().apply { time = currentDate }
        val normalizedCurrentDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentCal.get(Calendar.YEAR))
            set(Calendar.MONTH, currentCal.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, currentCal.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        when {
            normalizedCurrentDate.time == today.time -> "Today"
            normalizedCurrentDate.time == tomorrow.time -> "Tomorrow"
            normalizedCurrentDate.time == weekend.time -> "Weekend"
            else -> "DateTime" // Default to DateTime for custom dates
        }
    } else {
        "" // No selection if no date is set
    }

    // Remember which option is currently selected, initialized with the current date's value
    val selectedOption = remember { mutableStateOf(initialSelection) }

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

    // Define colors
    val iosBlue = IOSColors.Blue
    val grayColor = IOSColors.ButtonGray
    val grayBorder = IOSColors.ButtonGrayBorder

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
            backgroundColor = if (selectedOption.value == "Today") iosBlue.copy(alpha = 0.15f) else grayColor,
            borderColor = if (selectedOption.value == "Today") iosBlue else grayBorder,
            textColor = if (selectedOption.value == "Today") IOSColors.Black else IOSColors.DarkGrayText,
            onClick = {
                selectedOption.value = "Today"
                onTodaySelected()
            })

        // Tomorrow date button
        DateButton(
            date = tomorrow.toString(),
            label = "Tomorrow",
            backgroundColor = if (selectedOption.value == "Tomorrow") iosBlue.copy(alpha = 0.15f) else grayColor,
            borderColor = if (selectedOption.value == "Tomorrow") iosBlue else grayBorder,
            textColor = if (selectedOption.value == "Tomorrow") IOSColors.Black else IOSColors.DarkGrayText,
            onClick = {
                selectedOption.value = "Tomorrow"
                onTomorrowSelected()
            })

        if (tomorrow != weekend) {

            // Next Weekend date button
            DateButton(
                date = weekend.toString(),
                label = "Weekend",
                backgroundColor = if (selectedOption.value == "Weekend") iosBlue.copy(alpha = 0.15f) else grayColor,
                borderColor = if (selectedOption.value == "Weekend") iosBlue else grayBorder,
                textColor = if (selectedOption.value == "Weekend") IOSColors.Black else IOSColors.DarkGrayText,
                onClick = {
                    selectedOption.value = "Weekend"
                    onNextWeekendSelected()
                })
        }

        // Date & Time button
        DateButton(
            icon = Icons.Default.DateRange,
            date = "",
            label = "Calendar",
            backgroundColor = if (selectedOption.value == "DateTime") iosBlue.copy(alpha = 0.15f) else grayColor,
            borderColor = if (selectedOption.value == "DateTime") iosBlue else grayBorder,
            textColor = if (selectedOption.value == "DateTime") IOSColors.Black else IOSColors.DarkGrayText,
            onClick = {
                selectedOption.value = "DateTime"
                onDateTimeSelected()
            })
    }
}

@Composable
fun DateButton(
    icon: ImageVector? = null,
    date: String,
    label: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
    borderColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.width(80.dp)
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
            if (icon == null) {

                Text(
                    text = date,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            } else {
                Icon(
                    imageVector = icon, contentDescription = "Add Reminder"
                )
            }
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
            selectedColor = IOSColors.Blue, // iOS Blue for selected
            unselectedColor = IOSColors.Gray,
            onClick = onCalendarClick
        )

        // Add the location, tag, favorite, and person icons similarly
    }
}

@Composable
fun ActionIcon(
    icon: ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    selectedColor: androidx.compose.ui.graphics.Color,
    unselectedColor: androidx.compose.ui.graphics.Color,
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