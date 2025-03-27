package com.ohmz.remindersapp.data.repository

import com.ohmz.remindersapp.data.api.ApiKeyConfig
import com.ohmz.remindersapp.data.api.ChatCompletionRequest
import com.ohmz.remindersapp.data.api.Message
import com.ohmz.remindersapp.data.api.OpenAIService
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing OpenAI services
 */
@Singleton
class OpenAIRepository @Inject constructor(
    private val openAIService: Lazy<OpenAIService>
) {
    /**
     * Get suggestions for a reminder based on its title and notes
     * @param title The reminder title
     * @param notes The reminder notes (optional)
     * @return List of suggestions if successful, empty list otherwise
     */
    suspend fun getSuggestionsForReminder(title: String, notes: String?): List<String> {
        try {
            // Create the prompt for the AI
            val prompt = buildReminderPrompt(title, notes)

            // Create the API request
            val request = ChatCompletionRequest(
                messages = listOf(
                    Message(role = "system", content = SYSTEM_PROMPT),
                    Message(role = "user", content = prompt)
                ),
                temperature = 0.7
            )

            // Get API key with "Bearer " prefix required by OpenAI
            val authHeader = "Bearer ${ApiKeyConfig.getOpenAIApiKey()}"

            // Make the API call
            val response = openAIService.get().getChatCompletion(authHeader, request)

            // Process the response
            if (response.isSuccessful && response.body() != null) {
                val content = response.body()?.choices?.firstOrNull()?.message?.content
                if (!content.isNullOrBlank()) {
                    // Parse the list of suggestions from the response
                    return parseSuggestionsFromResponse(content)
                }
            }

            return emptyList()
        } catch (e: Exception) {
            // Handle any exceptions and return empty list
            return emptyList()
        }
    }

    /**
     * Build the prompt for the AI based on reminder details
     */
    private fun buildReminderPrompt(title: String, notes: String?): String {
        val prompt = StringBuilder()
        prompt.append("Reminder Title: $title\n")
        if (!notes.isNullOrBlank()) {
            prompt.append("Reminder Notes: $notes\n")
        }
        prompt.append("\nBased on this reminder, suggest 3-5 actionable tips or improvements.")

        return prompt.toString()
    }

    /**
     * Parse suggestions from the AI response text
     */
    private fun parseSuggestionsFromResponse(content: String): List<String> {
        // Simple parsing: split by newlines and remove numbering/bullets
        return content.split("\n")
            .filter { it.isNotBlank() }
            .map {
                // Remove numbering (1., 2., etc.) and bullet points
                it.replace(Regex("^\\s*\\d+\\.\\s*"), "")
                    .replace(Regex("^\\s*[-•*]\\s*"), "")
                    .trim()
            }
            .filter { it.isNotBlank() }
            .take(5) // Limit to maximum 5 suggestions
    }

    companion object {
        // System prompt to guide the AI response
        private val SYSTEM_PROMPT = """
            You are a helpful AI assistant for a reminder app. Your role is to give actionable tips
            and suggestions for improving reminders or enhancing productivity. 
            Format your response as a bulleted or numbered list with 3-5 concise suggestions.
            Each suggestion should be clear, actionable, and directly related to the reminder.
            Do not include any general introduction or conclusion text.
        """.trimIndent()
    }
}