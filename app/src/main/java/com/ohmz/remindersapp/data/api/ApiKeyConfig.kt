package com.ohmz.remindersapp.data.api

/**
 * Configuration class for API keys
 * This class is separate to keep API keys secure and not exposed in code repositories
 */
class ApiKeyConfig private constructor() {
    companion object {
        // Replace with your actual OpenAI API key
        // Note: In a real production app, this should be stored securely
        // (e.g., encrypted preferences, Android Keystore, or fetched from a secure server)
        private const val OPENAI_API_KEY = "YOUR_API_KEY"

        /**
         * Get the OpenAI API key
         */
        fun getOpenAIApiKey(): String {
            return OPENAI_API_KEY
        }

        /**
         * Base URL for OpenAI API
         */
        const val OPENAI_BASE_URL = "https://api.openai.com/v1/"
    }
}