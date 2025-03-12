package com.ohmz.remindersapp.presentation.common.utils

import com.ohmz.remindersapp.domain.model.Reminder
import java.util.*

/**
 * Utility class for date-related operations in the Reminders app
 */
object DateUtils {
    /**
     * Find past due reminders
     */
    fun findPastDueReminders(reminders: List<Reminder>): List<Reminder> {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return reminders.filter { reminder ->
            reminder.dueDate?.let { date ->
                val reminderCal = Calendar.getInstance().apply { time = date }
                reminderCal.before(today) && !reminder.isCompleted
            } ?: false
        }
    }

    /**
     * Find today's reminders
     */
    fun findTodayReminders(reminders: List<Reminder>): List<Reminder> {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

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
        val tomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val dayAfterTomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 2)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return reminders.filter { reminder ->
            reminder.dueDate?.let { date ->
                val reminderCal = Calendar.getInstance().apply { time = date }
                isSameDay(reminderCal, tomorrow)
            } ?: false
        }
    }

    /**
     * Check if a calendar date is tomorrow
     */
    fun isTomorrow(cal: Calendar): Boolean {
        val tomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return isSameDay(cal, tomorrow)
    }

    /**
     * Check if two calendars represent the same day
     */
    fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * Group reminders by month
     */
    fun groupRemindersByMonth(reminders: List<Reminder>): Map<Pair<Int, Int>, List<Reminder>> {
        return reminders.filter { it.dueDate != null }
            .groupBy { reminder ->
                val cal = Calendar.getInstance().apply { time = reminder.dueDate!! }
                Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
            }
    }

    /**
     * Generate the next five days (starting from the day after tomorrow)
     */
    fun generateNextFiveDays(): List<Date> {
        val result = mutableListOf<Date>()
        val cal = Calendar.getInstance()

        // Start from the day after tomorrow
        cal.add(Calendar.DAY_OF_YEAR, 2)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        // Add next 5 days
        for (i in 0 until 5) {
            result.add(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }

        return result
    }
}