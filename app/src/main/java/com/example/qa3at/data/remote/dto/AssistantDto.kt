package com.example.qa3at.data.remote.dto

import com.example.qa3at.domain.model.RecommendationTier
import com.example.qa3at.domain.model.Venue
import com.example.qa3at.domain.model.VenueRecommendation

data class ChatRequest(
    val message: String,
    val context: ChatContext? = null
)

data class ChatContext(
    val city: String? = null,
    val date: String? = null,
    val guestCount: Int? = null,
    val budget: Double? = null
)

data class ChatResponse(
    val message: String,
    val recommendations: List<RecommendationDto>? = null
)

data class RecommendationDto(
    val venueId: String,
    val venueName: String,
    val venueNameAr: String,
    val tier: String,
    val reason: String,
    val reasonAr: String,
    val estimatedTotal: Double
) {
    fun toDomain(): VenueRecommendation = VenueRecommendation(
        venue = Venue(
            id = venueId,
            name = venueName,
            nameAr = venueNameAr,
            description = "",
            descriptionAr = "",
            address = "",
            addressAr = "",
            city = "",
            cityAr = "",
            latitude = null,
            longitude = null,
            capacity = 0,
            minCapacity = 0,
            pricePerPerson = 0.0,
            basePrice = 0.0,
            rating = 0.0,
            reviewCount = 0,
            photos = emptyList(),
            amenities = emptyList(),
            vendorId = ""
        ),
        tier = when (tier) {
            "BALANCED" -> RecommendationTier.BALANCED
            "LUXURY" -> RecommendationTier.LUXURY
            else -> RecommendationTier.BUDGET
        },
        reason = reason,
        reasonAr = reasonAr,
        estimatedTotal = estimatedTotal
    )
}
