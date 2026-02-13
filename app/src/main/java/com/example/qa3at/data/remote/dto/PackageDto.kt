package com.example.qa3at.data.remote.dto

import com.example.qa3at.domain.model.Addon
import com.example.qa3at.domain.model.Package
import com.example.qa3at.domain.model.PackageCategory
import com.example.qa3at.domain.model.PackageTier

data class PackageDto(
    val id: String,
    val name: String,
    val nameAr: String,
    val description: String,
    val descriptionAr: String,
    val tier: String,
    val category: String,
    val basePrice: Double,
    val pricePerPerson: Double,
    val imageUrl: String? = null,
    val isActive: Boolean = true,
    val items: List<PackageItemDto> = emptyList()
) {
    fun toDomain(): Package = Package(
        id = id,
        name = name,
        nameAr = nameAr,
        description = description,
        descriptionAr = descriptionAr,
        tier = when (tier) {
            "GOLD" -> PackageTier.GOLD
            "DIAMOND" -> PackageTier.DIAMOND
            else -> PackageTier.SILVER
        },
        category = when (category) {
            "DECORATION" -> PackageCategory.DECORATION
            "CATERING" -> PackageCategory.CATERING
            "PHOTOGRAPHY" -> PackageCategory.PHOTOGRAPHY
            "MUSIC" -> PackageCategory.MUSIC
            else -> PackageCategory.VENUE
        },
        basePrice = basePrice,
        pricePerPerson = pricePerPerson,
        imageUrl = imageUrl,
        items = items.map { it.name }
    )
}

data class PackageItemDto(
    val id: String,
    val name: String,
    val nameAr: String,
    val description: String? = null,
    val descriptionAr: String? = null,
    val quantity: Int = 1
)

data class AddonDto(
    val id: String,
    val name: String,
    val nameAr: String,
    val description: String,
    val descriptionAr: String,
    val category: String,
    val price: Double,
    val priceType: String,
    val imageUrl: String? = null,
    val isActive: Boolean = true
) {
    fun toDomain(): Addon = Addon(
        id = id,
        name = name,
        nameAr = nameAr,
        description = description,
        descriptionAr = descriptionAr,
        category = category,
        price = price,
        priceType = priceType,
        imageUrl = imageUrl
    )
}
