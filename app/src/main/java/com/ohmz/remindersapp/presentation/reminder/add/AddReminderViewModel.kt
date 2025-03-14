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
    val error: String? = null,
    val isModified: Boolean = false
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
     * Helper function to calculate if any changes have been made to the reminder
     */
    private fun isModified(
        title: String = _uiState.value.title,
        notes: String = _uiState.value.notes,
        dueDate: Date? = _uiState.value.dueDate,
        isFavorite: Boolean = _uiState.value.isFavorite,
        listId: Int? = _uiState.value.listId,
        priority: Priority = _uiState.value.priority
    ): Boolean {
        return title.isNotEmpty() || 
               notes.isNotEmpty() || 
               dueDate != null || 
               isFavorite || 
               listId != null || 
               priority != Priority.MEDIUM
    }

    /**
     * Updates the title of the reminder
     */
    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            isModified = isModified(title = title)
        )
    }

    /**
     * Updates the notes of the reminder
     */
    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(
            notes = notes,
            isModified = isModified(notes = notes)
        )
    }

    /**
     * Updates the due date of the reminder
     */
    fun updateDueDate(dueDate: Date?) {
        _uiState.value = _uiState.value.copy(
            dueDate = dueDate,
            isModified = isModified(dueDate = dueDate)
        )
    }

    /**
     * Updates the priority of the reminder
     */
    fun updatePriority(priority: Priority) {
        _uiState.value = _uiState.value.copy(
            priority = priority,
            isModified = isModified(priority = priority)
        )
    }

    /**
     * Toggles the favorite status of the reminder
     */
    fun toggleFavorite() {
        val newFavoriteStatus = !_uiState.value.isFavorite
        _uiState.value = _uiState.value.copy(
            isFavorite = newFavoriteStatus,
            isModified = isModified(isFavorite = newFavoriteStatus)
        )
    }
    
    /**
     * Directly sets the favorite status to a specific value
     */
    fun setFavorite(isFavorite: Boolean) {
        _uiState.value = _uiState.value.copy(
            isFavorite = isFavorite,
            isModified = isModified(isFavorite = isFavorite)
        )
    }

    /**
     * Adds a tag to the reminder
     */
    fun addTag(tag: String) {
        if (tag.isNotBlank() && !_uiState.value.tags.contains(tag)) {
            val newTags = _uiState.value.tags + tag
            _uiState.value = _uiState.value.copy(
                tags = newTags,
                isModified = true // Adding a tag means the reminder is modified
            )
        }
    }

    /**
     * Removes a tag from the reminder
     */
    fun removeTag(tag: String) {
        val newTags = _uiState.value.tags.filter { it != tag }
        _uiState.value = _uiState.value.copy(
            tags = newTags,
            isModified = true // Removing a tag means the reminder is modified
        )
    }

    /**
     * Updates the list for the reminder
     */
    fun updateList(list: ReminderList) {
        _uiState.value = _uiState.value.copy(
            listId = list.id, 
            selectedListName = list.name, 
            showListSelector = false,
            isModified = isModified(listId = list.id)
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
        _uiState.value = _uiState.value.copy(
            imageUri = imageUri,
            isModified = imageUri != null || isModified()
        )
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
     * Validates the current reminder data
     * @return Error message if validation fails, null if validation passes
     */
    private fun validateReminder(): String? {
        return if (_uiState.value.title.isBlank()) {
            "Title cannot be empty"
        } else {
            null
        }
    }
    
    /**
     * Creates a Reminder domain object from the current UI state
     */
    private fun createReminderFromState(state: AddReminderUiState): Reminder {
        return Reminder(
            title = state.title,
            notes = state.notes.ifBlank { null },
            dueDate = state.dueDate,
            isCompleted = false,
            isFavorite = state.isFavorite,
            priority = state.priority,
            tags = state.tags,
            listId = state.listId,
            imageUri = state.imageUri
        )
    }

    /**
     * Saves the reminder
     */
    fun saveReminder() {
        viewModelScope.launch {
            val state = _uiState.value
            
            // Validate the reminder
            val validationError = validateReminder()
            if (validationError != null) {
                _uiState.value = state.copy(error = validationError)
                return@launch
            }

            // Set loading state
            _uiState.value = state.copy(isLoading = true)

            try {
                // Create and save reminder
                val reminder = createReminderFromState(state)
                addReminderUseCase(reminder)
                
                // Update state with success
                _uiState.value = state.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                // Handle errors
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
                    availableLists = lists, 
                    isLoading = false,
                    isModified = false
                )
            } catch (e: Exception) {
                // Just reset to the default state
                _uiState.value = AddReminderUiState(
                    isLoading = false,
                    isModified = false
                )
            }
        }
    }
    
    /**
     * Checks if there are unsaved changes
     */
    fun hasUnsavedChanges(): Boolean {
        return _uiState.value.isModified
    }
}