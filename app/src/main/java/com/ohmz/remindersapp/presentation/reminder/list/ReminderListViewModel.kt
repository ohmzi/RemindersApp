package com.ohmz.remindersapp.presentation.reminder.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohmz.remindersapp.domain.model.Reminder
import com.ohmz.remindersapp.domain.model.ReminderType
import com.ohmz.remindersapp.domain.usecase.DeleteReminderUseCase
import com.ohmz.remindersapp.domain.usecase.GetRemindersUseCase
import com.ohmz.remindersapp.domain.usecase.ToggleReminderCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject


/**
 * UI state for the reminder list screen
 */
data class ReminderListUiState(
    val reminders: List<Reminder> = emptyList(),
    val selectedType: ReminderType = ReminderType.ALL,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReminderListViewModel @Inject constructor(
    private val getRemindersUseCase: GetRemindersUseCase,
    private val toggleReminderCompletionUseCase: ToggleReminderCompletionUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReminderListUiState(isLoading = true))
    val uiState: StateFlow<ReminderListUiState> = _uiState.asStateFlow()

    init {
        loadReminders()
    }

    /**
     * Loads all reminders
     */
    private fun loadReminders() {
        getRemindersUseCase()
            .onEach { reminders ->
                _uiState.value = _uiState.value.copy(
                    reminders = reminders,
                    isLoading = false
                )
            }
            .catch { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred while loading reminders"
                )
            }
            .launchIn(viewModelScope)
    }

    /**
     * Selects a specific reminder type to display
     */
    fun selectReminderType(type: ReminderType) {
        _uiState.value = _uiState.value.copy(selectedType = type)
    }

    /**
     * Toggles the completion status of a reminder
     */
    fun toggleReminderCompletion(reminder: Reminder) {
        viewModelScope.launch {
            try {
                toggleReminderCompletionUseCase(reminder)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error toggling reminder completion"
                )
            }
        }
    }

    /**
     * Deletes a reminder
     */
    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            try {
                deleteReminderUseCase(reminder)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error deleting reminder"
                )
            }
        }
    }

    /**
     * Returns filtered reminders based on the selected type
     */
    fun getFilteredReminders(): List<Reminder> {
        val allReminders = uiState.value.reminders
        return when (uiState.value.selectedType) {
            ReminderType.TODAY -> {
                allReminders.filter { reminder ->
                    reminder.dueDate?.let { date ->
                        isSameDay(date, Date())
                    } ?: false
                }
            }
            ReminderType.SCHEDULED -> allReminders.filter { it.dueDate != null }
            ReminderType.ALL -> allReminders
            ReminderType.FAVOURITE -> allReminders.filter { it.isFavorite }
            ReminderType.COMPLETED -> allReminders.filter { it.isCompleted }
        }
    }

    /**
     * Checks if two dates are on the same day
     */
    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Clears any error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}