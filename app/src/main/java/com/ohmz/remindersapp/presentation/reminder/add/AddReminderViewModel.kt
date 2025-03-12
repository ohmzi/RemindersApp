package com.ohmz.remindersapp.presentation.reminder.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohmz.remindersapp.domain.model.Priority
import com.ohmz.remindersapp.domain.model.Reminder
import com.ohmz.remindersapp.domain.model.ReminderAction
import com.ohmz.remindersapp.domain.model.ReminderList
import com.ohmz.remindersapp.domain.repository.ReminderListRepository
import com.ohmz.remindersapp.domain.usecase.AddReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * UI state for the add reminder screen
 */
data class AddReminderUiState(
    val title: String = "",
    val notes: String = "",
    val dueDate: Date? = null,
    val priority: Priority = Priority.MEDIUM,
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList(),
    val listId: Int? = null,
    val selectedListName: String? = null,
    val availableLists: List<ReminderList> = emptyList(),
    val showListSelector: Boolean = false,
    val imageUri: String? = null,
    val selectedAction: ReminderAction? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddReminderViewModel @Inject constructor(
    private val addReminderUseCase: AddReminderUseCase,
    private val reminderListRepository: ReminderListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddReminderUiState())
    val uiState: StateFlow<AddReminderUiState> = _uiState.asStateFlow()

    init {
        loadReminderLists()
    }

    /**
     * Loads all reminder lists
     */
    private fun loadReminderLists() {
        viewModelScope.launch {
            try {
                // Get all lists
                val lists = reminderListRepository.getAllLists().first()

                // Get default list if it exists
                val defaultList = reminderListRepository.getOrCreateDefaultList()

                _uiState.value = _uiState.value.copy(
                    // Only set listId and name if default list exists
                    listId = defaultList?.id,
                    selectedListName = defaultList?.name,
                    availableLists = lists
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load reminder lists: ${e.message}"
                )
            }
        }
    }

    /**
     * Updates the title of the reminder
     */
    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    /**
     * Updates the notes of the reminder
     */
    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    /**
     * Updates the due date of the reminder
     */
    fun updateDueDate(dueDate: Date?) {
        _uiState.value = _uiState.value.copy(dueDate = dueDate)
    }

    /**
     * Updates the priority of the reminder
     */
    fun updatePriority(priority: Priority) {
        _uiState.value = _uiState.value.copy(priority = priority)
    }

    /**
     * Toggles the favorite status of the reminder
     */
    fun toggleFavorite() {
        _uiState.value = _uiState.value.copy(isFavorite = !_uiState.value.isFavorite)
    }

    /**
     * Adds a tag to the reminder
     */
    fun addTag(tag: String) {
        if (tag.isNotBlank() && !_uiState.value.tags.contains(tag)) {
            _uiState.value = _uiState.value.copy(
                tags = _uiState.value.tags + tag
            )
        }
    }

    /**
     * Removes a tag from the reminder
     */
    fun removeTag(tag: String) {
        _uiState.value = _uiState.value.copy(tags = _uiState.value.tags.filter { it != tag })
    }

    /**
     * Updates the list for the reminder
     */
    fun updateList(list: ReminderList) {
        _uiState.value = _uiState.value.copy(
            listId = list.id, selectedListName = list.name, showListSelector = false
        )
    }

    /**
     * Shows or hides the list selector
     */
    fun toggleListSelector() {
        _uiState.value = _uiState.value.copy(
            showListSelector = !_uiState.value.showListSelector
        )
    }

    /**
     * Adds a new list and assigns the reminder to it
     */
    fun addAndSelectList(name: String) {
        viewModelScope.launch {
            try {
                val newList = ReminderList(name = name)
                val newId = reminderListRepository.addList(newList)

                // Refresh lists and select the new one
                val lists = reminderListRepository.getAllLists().first()

                _uiState.value = _uiState.value.copy(
                    listId = newId.toInt(),
                    selectedListName = name,
                    availableLists = lists,
                    showListSelector = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to create new list: ${e.message}"
                )
            }
        }
    }

    /**
     * Updates the image URI of the reminder
     */
    fun updateImageUri(imageUri: String?) {
        _uiState.value = _uiState.value.copy(imageUri = imageUri)
    }

    /**
     * Toggles the selected action
     */
    fun toggleAction(action: ReminderAction) {
        _uiState.value = _uiState.value.copy(
            selectedAction = if (_uiState.value.selectedAction == action) null else action
        )
    }

    /**
     * Saves the reminder
     */
    fun saveReminder() {
        viewModelScope.launch {
            val state = _uiState.value

            // Validate title
            if (state.title.isBlank()) {
                _uiState.value = state.copy(
                    error = "Title cannot be empty"
                )
                return@launch
            }

            // Removed list validation - list selection is now optional

            _uiState.value = state.copy(isLoading = true)

            try {
                val reminder = Reminder(
                    title = state.title,
                    notes = state.notes.ifBlank { null },
                    dueDate = state.dueDate,
                    isCompleted = false,
                    isFavorite = state.isFavorite,
                    priority = state.priority,
                    tags = state.tags,
                    listId = state.listId, // This can now be null
                    imageUri = state.imageUri
                )

                addReminderUseCase(reminder)

                _uiState.value = state.copy(
                    isLoading = false, isSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred while saving the reminder"
                )
            }
        }
    }

    /**
     * Clears any error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Resets the success state
     */
    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }

    /**
     * Resets the entire UI state to its initial values
     * Should be called when the add reminder sheet is closed/dismissed
     */
    fun resetState() {
        viewModelScope.launch {
            try {
                // Get all lists for the dropdown, but don't select any by default
                val lists = reminderListRepository.getAllLists().first()

                // Reset to initial state but keep the loaded lists
                _uiState.value = AddReminderUiState(
                    availableLists = lists, isLoading = false
                )
            } catch (e: Exception) {
                // Just reset to the default state
                _uiState.value = AddReminderUiState(isLoading = false)
            }
        }
    }
}