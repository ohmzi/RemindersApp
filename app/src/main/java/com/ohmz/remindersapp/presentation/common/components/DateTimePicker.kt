package com.ohmz.remindersapp.presentation.common.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import java.util.Date

@Composable
fun DateTimePicker(
    initialDate: Date = Date(),
    onDateTimeSelected: (Date) -> Unit,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply {
        time = initialDate
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            
            // Set time to 11:59 PM
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            
            // Return the date with time set to 11:59 PM
            onDateTimeSelected(calendar.time)
        },
        // Initial date values
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.setOnCancelListener {
        onDismiss()
    }

    // Show the date picker immediately
    datePickerDialog.show()
}