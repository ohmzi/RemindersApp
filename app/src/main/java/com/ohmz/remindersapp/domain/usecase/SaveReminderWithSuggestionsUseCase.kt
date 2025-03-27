package com.ohmz.remindersapp.domain.usecase

import com.ohmz.remindersapp.domain.model.Reminder
import com.ohmz.remindersapp.domain.repository.ReminderRepository
import javax.inject.Inject

/**
 * Use case to save a reminder with AI suggestions
 */
class SaveReminderWithSuggestionsUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val getAiSuggestionsUseCase: GetAiSuggestionsUseCase
) {
    /**
     * Save a reminder with AI suggestions
     * @param reminder The reminder to save
     * @param shouldFetchSuggestions Whether to fetch AI suggestions
     * @return Result with the updated reminder or error
     */
    suspend operator fun invoke(
        reminder: Reminder,
        shouldFetchSuggestions: Boolean = true
    ): Result<Reminder> {
        return try {
            // If we should fetch suggestions and reminder is eligible
            val updatedReminder = if (shouldFetchSuggestions &&
                !reminder.hasAiSuggestions &&
                reminder.title.isNotBlank()
            ) {

                // Get AI suggestions
                val suggestionsResult = getAiSuggestionsUseCase(reminder)

                if (suggestionsResult.isSuccess) {
                    // Update the reminder with suggestions
                    reminder.copy(
                        hasAiSuggestions = true,
                        aiSuggestions = suggestionsResult.getOrDefault(emptyList())
                    )
                } else {
                    // Keep original reminder if suggestions fail
                    reminder
                }
            } else {
                // Keep original reminder
                reminder
            }

            // Save the reminder (with or without suggestions)
            val savedId = reminderRepository.saveReminder(updatedReminder)

            // Return the saved reminder with its ID
            Result.success(updatedReminder.copy(id = savedId.toInt()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}