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

            // After date is selected, show the time picker
            val timePickerDialog = TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)

                    // Return the complete date with time
                    onDateTimeSelected(calendar.time)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false // 12-hour format
            )

            timePickerDialog.setOnCancelListener {
                onDismiss()
            }

            timePickerDialog.show()
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