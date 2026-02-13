package com.example.qa3at.domain.model

data class CateringPackage(
    val id: String,
    val providerName: String,
    val providerNameAr: String,
    val packageName: String,
    val packageNameAr: String,
    val description: String,
    val descriptionAr: String,
    val pricePerPerson: Double,
    val tier: CateringTier,
    val menuItems: List<String>,
    val menuItemsAr: List<String>,
    val imageUrl: String
)

enum class CateringTier {
    THE_BEST,       // الأفضل
    VERY_EXCELLENT, // ممتاز جداً
    EXCELLENT       // ممتاز (الراقي)
}
