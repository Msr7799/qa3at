package com.example.qa3at.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class User(
    val id: String,
    val email: String,
    val name: String,
    val phone: String?,
    val role: UserRole = UserRole.USER
)

enum class UserRole {
    USER, ADMIN, VENDOR
}

data class Venue(
    val id: String,
    val name: String,
    val nameAr: String,
    val description: String,
    val descriptionAr: String,
    val address: String,
    val addressAr: String,
    val city: String,
    val cityAr: String,
    val latitude: Double?,
    val longitude: Double?,
    val capacity: Int,
    val minCapacity: Int,
    val pricePerPerson: Double,
    val basePrice: Double,
    val rating: Double,
    val reviewCount: Int,
    val photos: List<String>,
    val amenities: List<String>,
    val vendorId: String,
    val isActive: Boolean = true,
    val isFeatured: Boolean = false
)

data class VenuePhoto(
    val id: String,
    val url: String,
    val caption: String?,
    val isPrimary: Boolean = false
)

data class TimeSlot(
    val id: String,
    val name: String,
    val nameAr: String,
    val startTime: String,
    val endTime: String
)

data class VenueAvailability(
    val id: String,
    val venueId: String,
    val date: String,
    val slotId: String,
    val isAvailable: Boolean,
    val priceOverride: Double?
)

data class Package(
    val id: String,
    val name: String,
    val nameAr: String,
    val description: String,
    val descriptionAr: String,
    val tier: PackageTier,
    val category: PackageCategory,
    val basePrice: Double,
    val pricePerPerson: Double,
    val items: List<String> = emptyList(),
    val imageUrl: String? = null
)

enum class PackageTier {
    SILVER, GOLD, DIAMOND
}

enum class PackageCategory {
    VENUE, DECORATION, CATERING, PHOTOGRAPHY, MUSIC
}

data class PackageItem(
    val id: String,
    val name: String,
    val nameAr: String,
    val description: String?,
    val descriptionAr: String?,
    val quantity: Int
)

data class Addon(
    val id: String,
    val name: String,
    val nameAr: String,
    val description: String,
    val descriptionAr: String,
    val category: String,
    val price: Double,
    val priceType: String,
    val imageUrl: String? = null
)

enum class AddonCategory {
    STAGE, FLOWERS, DECORATION, CATERING, CAKE, PHOTOGRAPHY, MUSIC
}

enum class PriceType {
    FIXED, PER_PERSON, PER_HOUR
}

data class Booking(
    val id: String,
    val userId: String,
    val venueId: String,
    val venueName: String = "",
    val venueNameAr: String = "",
    val venueImage: String? = null,
    val date: String,
    val slotId: String,
    val slotName: String = "",
    val guestCount: Int,
    val status: BookingStatus,
    val subtotal: Double,
    val tax: Double,
    val total: Double,
    val notes: String? = null,
    val createdAt: String = ""
)

enum class BookingStatus {
    PENDING, CONFIRMED, CANCELLED, COMPLETED
}

data class BookingItem(
    val id: String,
    val type: BookingItemType,
    val referenceId: String,
    val name: String,
    val nameAr: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double
)

enum class BookingItemType {
    VENUE, PACKAGE, ADDON
}

data class Payment(
    val id: String,
    val bookingId: String,
    val amount: Double,
    val currency: String = "BHD",
    val method: PaymentMethod,
    val status: PaymentStatus,
    val transactionId: String?,
    val paidAt: String?
)

enum class PaymentMethod {
    CARD, APPLE_PAY, MADA, BANK_TRANSFER
}

enum class PaymentStatus {
    PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED
}

data class Review(
    val id: String,
    val userId: String,
    val userName: String,
    val venueId: String,
    val rating: Int,
    val comment: String?,
    val createdAt: String
)

data class City(
    val id: String,
    val name: String,
    val nameAr: String
)

data class SearchFilters(
    val city: String = "",
    val type: String = "",
    val date: String = "",
    val guests: Int = 0,
    val slotId: String = "",
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minRating: Double? = null,
    val minCapacity: Int? = null,
    val amenities: List<String> = emptyList(),
    val sortBy: SortOption = SortOption.RECOMMENDED
)

enum class SortOption {
    RECOMMENDED, PRICE_LOW, PRICE_HIGH, RATING, CAPACITY
}

data class AssistantMessage(
    val id: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val recommendations: List<VenueRecommendation>? = null
)

enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}

data class VenueRecommendation(
    val venue: Venue,
    val tier: RecommendationTier,
    val reason: String,
    val reasonAr: String,
    val estimatedTotal: Double
)

enum class RecommendationTier {
    BUDGET, BALANCED, LUXURY
}
