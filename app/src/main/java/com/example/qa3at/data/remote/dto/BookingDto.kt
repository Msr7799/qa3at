package com.example.qa3at.data.remote.dto

import com.example.qa3at.domain.model.Booking
import com.example.qa3at.domain.model.BookingStatus

data class CreateBookingRequest(
    val venueId: String,
    val slotId: String,
    val date: String,
    val guestCount: Int,
    val packageIds: List<String>? = null,
    val addonIds: List<String>? = null,
    val notes: String? = null
)

data class BookingDto(
    val id: String,
    val date: String,
    val guestCount: Int,
    val status: String,
    val notes: String? = null,
    val subtotal: Double,
    val tax: Double,
    val total: Double,
    val createdAt: String,
    val venue: VenueDto? = null,
    val slot: TimeSlotDto? = null,
    val items: List<BookingItemDto> = emptyList()
) {
    fun toDomain(): Booking = Booking(
        id = id,
        userId = "",
        venueId = venue?.id ?: "",
        venueName = venue?.name ?: "",
        venueNameAr = venue?.nameAr ?: "",
        venueImage = venue?.photos?.firstOrNull()?.url,
        slotId = slot?.id ?: "",
        slotName = slot?.name ?: "",
        date = date,
        guestCount = guestCount,
        status = when (status) {
            "CONFIRMED" -> BookingStatus.CONFIRMED
            "CANCELLED" -> BookingStatus.CANCELLED
            "COMPLETED" -> BookingStatus.COMPLETED
            else -> BookingStatus.PENDING
        },
        subtotal = subtotal,
        tax = tax,
        total = total,
        notes = notes,
        createdAt = createdAt
    )
}

data class BookingItemDto(
    val id: String,
    val type: String,
    val name: String,
    val nameAr: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double
)
