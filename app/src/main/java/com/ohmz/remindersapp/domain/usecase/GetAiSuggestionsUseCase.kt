package com.ohmz.remindersapp.domain.usecase

import com.ohmz.remindersapp.data.repository.OpenAIRepository
import com.ohmz.remindersapp.domain.model.Reminder
import javax.inject.Inject

/**
 * Use case for getting AI suggestions for a reminder
 */
class GetAiSuggestionsUseCase @Inject constructor(
    private val openAIRepository: OpenAIRepository
) {
    /**
     * Get AI suggestions for a reminder
     * @param reminder The reminder to get suggestions for
     * @return Result with list of suggestions or error
     */
    suspend operator fun invoke(reminder: Reminder): Result<List<String>> {
        return try {
            // Only query if there are no existing suggestions or if content changed
            if (!reminder.hasAiSuggestions || reminder.aiSuggestions.isEmpty()) {
                val suggestions = openAIRepository.getSuggestionsForReminder(
                    title = reminder.title,
                    notes = reminder.notes
                )

                if (suggestions.isNotEmpty()) {
                    Result.success(suggestions)
                } else {
                    Result.failure(Exception("Failed to get suggestions"))
                }
            } else {
                // Return existing suggestions
                Result.success(reminder.aiSuggestions)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}