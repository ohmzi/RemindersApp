package com.ohmz.remindersapp.presentation.common.utils

import com.ohmz.remindersapp.domain.model.Reminder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Utility class for date-related operations in the Reminders app
 */
object DateUtils {
    private val dateTimeFormat = SimpleDateFormat("EEEE, h:mm a", Locale.getDefault())
    private val dateWithTimeFormat = SimpleDateFormat("EEEE, MMM d, h:mm a", Locale.getDefault())
    
    /**
     * Creates a Calendar instance set to the start of the day
     * @param daysToAdd Number of days to add to today (default 0)
     * @return Calendar instance set to the start of the specified day
     */
    private fun getStartOfDay(daysToAdd: Int = 0): Calendar {
        return Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, daysToAdd)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    /**
     * Format a date with time in iOS style (e.g., "Today, 1:28 PM" or "Mar 15, 2:45 PM")
     */
    fun formatDateWithTime(date: Date): String {
        val today = Calendar.getInstance()
        val inputDate = Calendar.getInstance().apply { time = date }

        return if (isSameDay(inputDate, today)) {
            "Today, ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)}"
        } else {
            dateWithTimeFormat.format(date)
        }
    }

    /**
     * Find past due reminders
     */
    fun findPastDueReminders(reminders: List<Reminder>): List<Reminder> {
        val today = getStartOfDay()

        return reminders.filter { reminder ->
            reminder.dueDate?.let { date ->
                val reminderCal = Calendar.getInstance().apply { time = date }
                reminderCal.before(today) && !reminder.isCompleted
            } ?: false
        }
    }
    
    /**
     * Find past due reminders including completed ones (for consistent UI display)
     */
    fun findPastDueRemindersIncludingCompleted(reminders: List<Reminder>): List<Reminder> {
        val today = getStartOfDay()

        return reminders.filter { reminder ->
            reminder.dueDate?.let { date ->
                val reminderCal = Calendar.getInstance().apply { time = date }
                reminderCal.before(today) // Only check date, not completion status
            } ?: false
        }
    }

    /**
     * Find today's reminders
     */
    fun findTodayReminders(reminders: List<Reminder>): List<Reminder> {
        val today = getStartOfDay()

        return reminders.filter { reminder ->
            reminder.dueDate?.let { date ->
                val reminderCal = Calendar.getInstance().apply { time = date }
                isSameDay(reminderCal, today)
            } ?: false
        }
    }

    /**
     * Find tomorrow's reminders
     */
    fun findTomorrowReminders(reminders: List<Reminder>): List<Reminder> {
        val tomorrow = getStartOfDay(1)

        return reminders.filter { reminder ->
            reminder.dueDate?.let { date ->
                val reminderCal = Calendar.getInstance().apply { time = date }
                isSameDay(reminderCal, tomorrow)
            } ?: false
        }
    }

    /**
     * Compare the sent date against today
     */
    fun pastDue(date: Date): Boolean {
        val today = getStartOfDay()
        val reminderCal = Calendar.getInstance().apply { time = date }
        return reminderCal.before(today)
    }

    /**
     * Check if a calendar date is tomorrow
     */
    fun isTomorrow(cal: Calendar): Boolean {
        val tomorrow = getStartOfDay(1)
        return isSameDay(cal, tomorrow)
    }

    /**
     * Check if two calendars represent the same day
     */
    fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(
            Calendar.MONTH
        ) && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * Group reminders by month
     */
    fun groupRemindersByMonth(reminders: List<Reminder>): Map<Pair<Int, Int>, List<Reminder>> {
        return reminders.filter { it.dueDate != null }.groupBy { reminder ->
            val cal = Calendar.getInstance().apply { time = reminder.dueDate!! }
            Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
        }
    }

    /**
     * Generate the next five days (starting from the day after tomorrow)
     */
    fun generateNextFiveDays(): List<Date> {
        val result = mutableListOf<Date>()
        
        // Start from the day after tomorrow (2 days from now)
        for (i in 2 until 7) {
            result.add(getStartOfDay(i).time)
        }

        return result
    }
}