package com.example.qa3at.data.remote.dto

import com.example.qa3at.domain.model.City
import com.example.qa3at.domain.model.TimeSlot
import com.example.qa3at.domain.model.Venue

data class VenueListResponse(
    val data: List<VenueDto>,
    val meta: MetaDto
)

data class MetaDto(
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int
)

data class VenueDto(
    val id: String,
    val name: String,
    val nameAr: String,
    val description: String,
    val descriptionAr: String,
    val address: String,
    val addressAr: String,
    val city: String,
    val cityAr: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val capacity: Int,
    val minCapacity: Int,
    val pricePerPerson: Double,
    val basePrice: Double,
    val rating: Double,
    val reviewCount: Int,
    val amenities: List<String> = emptyList(),
    val photos: List<VenuePhotoDto> = emptyList(),
    val vendorId: String,
    val isActive: Boolean = true
) {
    fun toDomain(): Venue = Venue(
        id = id,
        name = name,
        nameAr = nameAr,
        description = description,
        descriptionAr = descriptionAr,
        address = address,
        addressAr = addressAr,
        city = city,
        cityAr = cityAr,
        latitude = latitude,
        longitude = longitude,
        capacity = capacity,
        minCapacity = minCapacity,
        pricePerPerson = pricePerPerson,
        basePrice = basePrice,
        rating = rating,
        reviewCount = reviewCount,
        amenities = amenities,
        photos = photos.map { it.url },
        vendorId = vendorId,
        isActive = isActive
    )
}

data class VenuePhotoDto(
    val id: String,
    val url: String,
    val caption: String? = null,
    val captionAr: String? = null,
    val isPrimary: Boolean = false,
    val sortOrder: Int = 0
)

data class CityDto(
    val id: String,
    val name: String,
    val nameAr: String
) {
    fun toDomain(): City = City(
        id = id,
        name = name,
        nameAr = nameAr
    )
}

data class TimeSlotDto(
    val id: String,
    val name: String,
    val nameAr: String,
    val startTime: String,
    val endTime: String,
    val isActive: Boolean = true
) {
    fun toDomain(): TimeSlot = TimeSlot(
        id = id,
        name = name,
        nameAr = nameAr,
        startTime = startTime,
        endTime = endTime
    )
}

data class AvailabilityDto(
    val id: String,
    val date: String,
    val isAvailable: Boolean,
    val priceOverride: Double? = null,
    val slot: TimeSlotDto
)
