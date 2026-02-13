package com.example.qa3at.data.local

import android.content.Context
import com.example.qa3at.R
import com.example.qa3at.domain.model.Venue
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// ───── JSON Data Classes ─────

@JsonClass(generateAdapter = true)
data class JsonVenuesRoot(
    val sourceBaseUrl: String? = null,
    val generatedAt: String? = null,
    val hotels: List<JsonVenueItem> = emptyList(),
    val independentHalls: List<JsonVenueItem> = emptyList(),
    val imagePolicyNote: String? = null
)

@JsonClass(generateAdapter = true)
data class JsonVenueItem(
    val number: Int? = null,
    val starRating: Int? = null,
    val nid: String? = null,
    val titleArabic: String? = null,
    val titleEnglish: String? = null,
    val url: String? = null,
    val fullUrl: String? = null,
    val featured: String? = null,
    val featuredPlus: Boolean? = null,
    val hasPackages: String? = null,
    val maxGuestsOutdoor: String? = null,
    val maxGuestsIndoor: String? = null,
    val price: String? = null,
    val category: String? = null,
    val city: String? = null,
    val mainImage: String? = null,
    val photos: List<String>? = null,
    val descriptionEN: String? = null,
    val imageUrls: List<String>? = null,
    val imageSource: String? = null
)

// ───── Repository ─────

@Singleton
class VenueJsonRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi
) {
    private var cachedVenues: List<Venue>? = null

    fun getAllVenues(): List<Venue> {
        cachedVenues?.let { return it }

        val jsonString = context.resources.openRawResource(R.raw.bahrain_wedding_venues)
            .bufferedReader()
            .use { it.readText() }

        val adapter = moshi.adapter(JsonVenuesRoot::class.java)
        val root = adapter.fromJson(jsonString) ?: return emptyList()

        val allItems = mutableListOf<JsonVenueItem>()
        allItems.addAll(root.hotels)
        allItems.addAll(root.independentHalls)

        val venues = allItems.mapNotNull { item -> item.toVenue() }
        cachedVenues = venues
        return venues
    }

    fun getVenueById(id: String): Venue? {
        return getAllVenues().find { it.id == id }
    }

    fun searchVenues(
        query: String = "",
        category: String = "all",
        city: String = "all",
        sortBy: String = "default"
    ): List<Venue> {
        var result = getAllVenues()

        // Filter by search query
        if (query.isNotBlank()) {
            val q = query.lowercase()
            result = result.filter {
                it.name.lowercase().contains(q) ||
                it.nameAr.contains(q) ||
                it.description.lowercase().contains(q) ||
                it.descriptionAr.contains(q) ||
                it.city.lowercase().contains(q) ||
                it.cityAr.contains(q)
            }
        }

        // Filter by category
        if (category != "all") {
            result = result.filter { venue ->
                when (category) {
                    "hotel", "hotels" -> venue.vendorId == "hotel"
                    "hall", "halls" -> venue.vendorId == "hall"
                    else -> true
                }
            }
        }

        // Filter by city
        if (city != "all") {
            result = result.filter { it.city.equals(city, ignoreCase = true) }
        }

        // Sort
        result = when (sortBy) {
            "price_low" -> result.sortedBy { it.pricePerPerson }
            "price_high" -> result.sortedByDescending { it.pricePerPerson }
            "rating" -> result.sortedByDescending { it.rating }
            "capacity" -> result.sortedByDescending { it.capacity }
            "name" -> result.sortedBy { it.name }
            else -> result // default order from JSON
        }

        return result
    }

    fun getCities(): List<String> {
        return getAllVenues().map { it.city }.distinct().sorted()
    }
}

// ───── Mapper ─────

private fun JsonVenueItem.toVenue(): Venue? {
    val id = nid ?: return null

    val capacity = parseCapacity(maxGuestsIndoor)
    val outdoorCap = parseCapacity(maxGuestsOutdoor)
    val parsedPrice = parsePrice(price)
    val stars = starRating ?: 0

    // Collect all image URLs: mainImage + imageUrls (deduplicated)
    val allPhotos = mutableListOf<String>()
    mainImage?.takeIf { it.isNotBlank() }?.let { allPhotos.add(it) }
    imageUrls?.filter { it.isNotBlank() }?.forEach { url ->
        if (url !in allPhotos) allPhotos.add(url)
    }
    photos?.filter { it.isNotBlank() }?.forEach { url ->
        if (url !in allPhotos) allPhotos.add(url)
    }

    val isHotel = category?.lowercase()?.contains("hotel") == true
    val cityAr = mapCityToArabic(city ?: "")

    return Venue(
        id = id,
        name = titleEnglish ?: (titleArabic ?: ""),
        nameAr = titleArabic ?: (titleEnglish ?: ""),
        description = descriptionEN ?: "",
        descriptionAr = descriptionEN ?: "", // JSON only has EN description
        address = "${city ?: ""}, Bahrain",
        addressAr = "$cityAr، البحرين",
        city = city ?: "",
        cityAr = cityAr,
        latitude = null,
        longitude = null,
        capacity = capacity,
        minCapacity = if (capacity > 0) (capacity / 5).coerceAtLeast(10) else 0,
        pricePerPerson = parsedPrice,
        basePrice = if (parsedPrice > 0) parsedPrice * 50 else 0.0, // estimate
        rating = stars.toDouble(),
        reviewCount = 0,
        photos = allPhotos,
        amenities = buildAmenities(hasPackages, isHotel),
        vendorId = if (isHotel) "hotel" else "hall",
        isActive = true
    )
}

private fun parseCapacity(text: String?): Int {
    if (text.isNullOrBlank()) return 0
    val clean = text.trim()

    // Handle "<50"
    if (clean.startsWith("<")) {
        return clean.removePrefix("<").trim().toIntOrNull() ?: 0
    }

    // Handle "100-200" → take max
    if (clean.contains("-")) {
        val parts = clean.split("-")
        return parts.lastOrNull()?.trim()?.toIntOrNull() ?: 0
    }

    // Handle "500"
    return clean.toIntOrNull() ?: 0
}

private fun parsePrice(text: String?): Double {
    if (text.isNullOrBlank()) return 0.0
    // Parse "12 BHD/person (avg)" or "8 BHD/person (avg)"
    val regex = Regex("""(\d+(?:\.\d+)?)\s*BHD""")
    val match = regex.find(text)
    return match?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0
}

private fun buildAmenities(hasPackages: String?, isHotel: Boolean): List<String> {
    val list = mutableListOf<String>()
    if (isHotel) {
        list.add("Parking")
        list.add("Catering")
    }
    if (hasPackages?.lowercase() == "yes") {
        list.add("Packages Available")
    }
    list.add("Sound System")
    return list
}

private fun mapCityToArabic(city: String): String {
    return when (city.lowercase()) {
        "manama" -> "المنامة"
        "sitra" -> "سترة"
        "a'ali", "aali" -> "عالي"
        "bani jamra" -> "بني جمرة"
        "riffa" -> "الرفاع"
        "muharraq" -> "المحرق"
        "hamad town" -> "مدينة حمد"
        "isa town" -> "مدينة عيسى"
        else -> city
    }
}
