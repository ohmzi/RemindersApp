package com.ohmz.remindersapp.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit interface for OpenAI API
 */
interface OpenAIService {
    @POST("chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: ChatCompletionRequest
    ): Response<ChatCompletionResponse>
}

/**
 * Request model for OpenAI Chat Completion API
 */
data class ChatCompletionRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>,
    val max_tokens: Int = 150,
    val temperature: Double = 0.7
)

/**
 * Message model for OpenAI Chat API
 */
data class Message(
    val role: String, // "system", "user", or "assistant"
    val content: String
)

/**
 * Response model from OpenAI API
 */
data class ChatCompletionResponse(
    val id: String,
    val `object`: String, // Using backticks to escape reserved keyword
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage
)

data class Choice(
    val index: Int,
    val message: Message,
    val finish_reason: String
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)