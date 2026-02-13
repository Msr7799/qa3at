package com.example.qa3at.data.repository

import com.example.qa3at.data.remote.ApiService
import com.example.qa3at.data.remote.dto.ChatContext
import com.example.qa3at.data.remote.dto.ChatRequest
import com.example.qa3at.domain.model.AssistantMessage
import com.example.qa3at.domain.model.MessageRole
import com.example.qa3at.domain.model.VenueRecommendation
import javax.inject.Inject
import javax.inject.Singleton

data class AssistantResponse(
    val message: String,
    val recommendations: List<VenueRecommendation>?
)

@Singleton
class AssistantRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun sendMessage(
        message: String,
        city: String? = null,
        date: String? = null,
        guestCount: Int? = null,
        budget: Double? = null
    ): Result<AssistantResponse> {
        return try {
            val context = if (city != null || date != null || guestCount != null || budget != null) {
                ChatContext(
                    city = city,
                    date = date,
                    guestCount = guestCount,
                    budget = budget
                )
            } else null

            val response = apiService.chat(
                ChatRequest(
                    message = message,
                    context = context
                )
            )

            if (response.isSuccessful) {
                val body = response.body()!!
                Result.Success(
                    AssistantResponse(
                        message = body.message,
                        recommendations = body.recommendations?.map { it.toDomain() }
                    )
                )
            } else {
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}
