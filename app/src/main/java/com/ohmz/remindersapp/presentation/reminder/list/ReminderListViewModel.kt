package com.ohmz.remindersapp.presentation.reminder.list

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohmz.remindersapp.domain.model.Reminder
import com.ohmz.remindersapp.domain.model.ReminderType
import com.ohmz.remindersapp.domain.usecase.DeleteReminderUseCase
import com.ohmz.remindersapp.domain.usecase.GetRemindersUseCase
import com.ohmz.remindersapp.domain.usecase.ToggleReminderCompletionUseCase
import com.ohmz.remindersapp.domain.usecase.ToggleReminderFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
data class ReminderCounts(
    val todayCount: Int = 0,
    val scheduledCount: Int = 0,
    val allCount: Int = 0,
    val favoriteCount: Int = 0,
    val completedCount: Int = 0
)

data class ReminderListUiState(
    val reminders: List<Reminder> = emptyList(),
    val selectedType: ReminderType = ReminderType.ALL,
    val isLoading: Boolean = false,
    val error: String? = null,
    val categoryCounts: ReminderCounts = ReminderCounts()
)

/**
 * Helper class to handle caching of reminder counts
 */
class ReminderCountsCache(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("reminder_counts_cache", Context.MODE_PRIVATE)
    
    fun saveCounts(counts: ReminderCounts) {
        sharedPreferences.edit().apply {
            putInt("today_count", counts.todayCount)
            putInt("scheduled_count", counts.scheduledCount)  
            putInt("all_count", counts.allCount)
            putInt("favorite_count", counts.favoriteCount)
            putInt("completed_count", counts.completedCount)
            apply()
        }
    }
    
    fun loadCounts(): ReminderCounts {
        return ReminderCounts(
            todayCount = sharedPreferences.getInt("today_count", 0),
            scheduledCount = sharedPreferences.getInt("scheduled_count", 0),
            allCount = sharedPreferences.getInt("all_count", 0),
            favoriteCount = sharedPreferences.getInt("favorite_count", 0),
            completedCount = sharedPreferences.getInt("completed_count", 0)
        )
    }
}

@HiltViewModel
class ReminderListViewModel @Inject constructor(
    private val getRemindersUseCase: GetRemindersUseCase,
    private val toggleReminderCompletionUseCase: ToggleReminderCompletionUseCase,
    private val toggleReminderFavoriteUseCase: ToggleReminderFavoriteUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val countsCache = ReminderCountsCache(context)
    
    // Initialize with cached counts to prevent zeros at startup
    private val cachedCounts = countsCache.loadCounts()
    
    private val _uiState = MutableStateFlow(
        ReminderListUiState(
            isLoading = true,
            categoryCounts = cachedCounts // Use cached counts immediately
        )
    )
    val uiState: StateFlow<ReminderListUiState> = _uiState.asStateFlow()

    init {
        loadReminders()
    }

    /**
     * Loads all reminders and updates the counts
     */
    private fun loadReminders() {
        getRemindersUseCase()
            .onEach { reminders ->
                // Calculate counts
                val todayCount = reminders.count { reminder ->
                    (reminder.dueDate?.let { date -> isSameDay(date, Date()) } ?: false) && !reminder.isCompleted
                }
                val scheduledCount = reminders.count { it.dueDate != null && !it.isCompleted }
                val allCount = reminders.size
                val favoriteCount = reminders.count { it.isFavorite }
                val completedCount = reminders.count { it.isCompleted }
                
                // Create new counts object
                val newCounts = ReminderCounts(
                    todayCount = todayCount,
                    scheduledCount = scheduledCount,
                    allCount = allCount,
                    favoriteCount = favoriteCount,
                    completedCount = completedCount
                )
                
                // Update UI state with reminders and counts
                _uiState.value = _uiState.value.copy(
                    reminders = reminders,
                    isLoading = false,
                    categoryCounts = newCounts
                )
                
                // Save counts to cache
                countsCache.saveCounts(newCounts)
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
     * Toggles the favorite status of a reminder
     */
    fun toggleReminderFavorite(reminder: Reminder, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                toggleReminderFavoriteUseCase.setFavorite(reminder, isFavorite)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error toggling reminder favorite status"
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
                    (reminder.dueDate?.let { date ->
                        isSameDay(date, Date())
                    } ?: false) && !reminder.isCompleted
                }
            }
            ReminderType.SCHEDULED -> allReminders.filter { it.dueDate != null && !it.isCompleted }
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