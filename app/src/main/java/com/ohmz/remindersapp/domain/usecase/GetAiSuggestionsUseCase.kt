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
    // Store the last content we generated suggestions for
    private var lastTitle: String? = null
    private var lastNotes: String? = null
    
    /**
     * Get AI suggestions for a reminder
     * @param reminder The reminder to get suggestions for
     * @param forceRefresh Whether to force a refresh of suggestions even if content hasn't changed
     * @return Result with list of suggestions or error
     */
    suspend operator fun invoke(
        reminder: Reminder, 
        forceRefresh: Boolean = false
    ): Result<List<String>> {
        return try {
            // Check if content has changed since last suggestion generation
            val contentChanged = hasContentChanged(reminder.title, reminder.notes)
            
            // Generate new suggestions if:
            // 1. No existing suggestions, OR
            // 2. Content has changed since last generation, OR
            // 3. Force refresh is requested
            if (!reminder.hasAiSuggestions || 
                reminder.aiSuggestions.isEmpty() || 
                contentChanged || 
                forceRefresh
            ) {
                val suggestions = openAIRepository.getSuggestionsForReminder(
                    title = reminder.title,
                    notes = reminder.notes
                )

                // Store this content as the last one we generated suggestions for
                lastTitle = reminder.title
                lastNotes = reminder.notes

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
    
    /**
     * Check if the content has changed since the last time we generated suggestions
     */
    private fun hasContentChanged(title: String, notes: String?): Boolean {
        return title != lastTitle || notes != lastNotes
    }
}