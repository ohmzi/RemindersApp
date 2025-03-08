package com.ohmz.remindersapp.ui.reminderScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohmz.remindersapp.data.ReminderEntity
import com.ohmz.remindersapp.data.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {

    // Expose a Flow of all reminders as StateFlow
    val reminders = repository.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addReminder(title: String, description: String?, dueDate: Long?) {
        viewModelScope.launch {
            val reminder = ReminderEntity(
                title = title,
                description = description,
                dueDate = dueDate,
                isCompleted = false
            )
            repository.insertReminder(reminder)
        }
    }

    fun updateReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            repository.updateReminder(reminder)
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }

    fun toggleCompletion(reminder: ReminderEntity) {
        val updated = reminder.copy(isCompleted = !reminder.isCompleted)
        updateReminder(updated)
    }
}
