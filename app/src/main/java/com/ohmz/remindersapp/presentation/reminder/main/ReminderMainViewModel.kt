package com.ohmz.remindersapp.presentation.reminder.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohmz.remindersapp.domain.model.ReminderList
import com.ohmz.remindersapp.domain.repository.ReminderListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReminderMainUiState(
    val lists: List<ReminderList> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReminderMainViewModel @Inject constructor(
    private val reminderListRepository: ReminderListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReminderMainUiState(isLoading = true))
    val uiState: StateFlow<ReminderMainUiState> = _uiState.asStateFlow()

    init {
        loadLists()
    }

    private fun loadLists() {
        viewModelScope.launch {
            try {
                reminderListRepository.getAllLists().collect { lists ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            lists = lists,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Failed to load lists: ${e.message}"
                    )
                }
            }
        }
    }

    fun addList(name: String, color: String = "#007AFF") {
        viewModelScope.launch {
            try {
                val newList = ReminderList(name = name, color = color)
                reminderListRepository.addList(newList)
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = "Failed to add list: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Rename an existing list
     */
    fun renameList(list: ReminderList, newName: String) {
        if (newName.isBlank() || newName == list.name) return

        viewModelScope.launch {
            try {
                val updatedList = list.copy(name = newName)
                reminderListRepository.updateList(updatedList)
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = "Failed to rename list: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Update the color of an existing list
     */
    fun updateListColor(list: ReminderList, newColor: String) {
        if (newColor == list.color) return

        viewModelScope.launch {
            try {
                val updatedList = list.copy(color = newColor)
                reminderListRepository.updateList(updatedList)
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = "Failed to update list color: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Delete an existing list
     */
    fun deleteList(list: ReminderList) {
        viewModelScope.launch {
            try {
                reminderListRepository.deleteList(list)
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = "Failed to delete list: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { currentState ->
            currentState.copy(error = null)
        }
    }
}