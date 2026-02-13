package com.example.qa3at.data.repository

import com.example.qa3at.domain.model.CateringPackage
import com.example.qa3at.domain.model.CateringTier
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CateringRepository @Inject constructor() {

    fun getCateringPackages(): List<CateringPackage> {
        return listOf(
            // Tier 1: The Best (الأفضل - Economic)
            CateringPackage(
                id = "cat_1",
                providerName = "Al Abraaj Catering",
                providerNameAr = "مطعم الأبراج",
                packageName = "Classic Arabic Buffet",
                packageNameAr = "البوفيه العربي الكلاسيكي",
                description = "Traditional Bahraini hospitality with favorite local dishes.",
                descriptionAr = "ضيافة بحرينية أصيلة مع أشهى الأطباق المحلية.",
                pricePerPerson = 4.5,
                tier = CateringTier.THE_BEST,
                menuItems = listOf("Mixed Grills", "Chicken Machboos", "Hummus & Tabbouleh", "Umm Ali", "Soft Drinks"),
                menuItemsAr = listOf("مشويات مشكلة", "مجبوس دجاج", "حمص وتبولة", "أم علي", "مشروبات غازية"),
                imageUrl = "https://images.unsplash.com/photo-1555244162-803834f70033"
            ),
            CateringPackage(
                id = "cat_2",
                providerName = "Marmariz Restaurant",
                providerNameAr = "مطعم مرمريز",
                packageName = "Family Feast",
                packageNameAr = "وليمة العائلة",
                description = "Perfect for casual family weddings and gatherings.",
                descriptionAr = "مثالية لحفلات الزفاف العائلية والتجمعات.",
                pricePerPerson = 3.9,
                tier = CateringTier.THE_BEST,
                menuItems = listOf("Chicken Tikka", "Biryani Rice", "Fatoush", "Fresh Juices", "Custard"),
                menuItemsAr = listOf("دجاج تكا", "رز برياني", "فتوش", "عصائر طازجة", "كاسترد"),
                imageUrl = "https://images.unsplash.com/photo-1631515243349-e0cb75fb8d3a"
            ),

            // Tier 2: Very Excellent (ممتاز جداً - Premium)
            CateringPackage(
                id = "cat_3",
                providerName = "Gulf Hotel Catering",
                providerNameAr = "ضيافة فندق الخليج",
                packageName = "Royal Banquet",
                packageNameAr = "المأدبة الملكية",
                description = "A luxurious international buffet experience.",
                descriptionAr = "تجربة بوفيه عالمية فاخرة.",
                pricePerPerson = 9.5,
                tier = CateringTier.VERY_EXCELLENT,
                menuItems = listOf("Roast Lamb Ouzi", "Live Pasta Station", "Sushi Platter", "Chocolate Fountain", "Assorted Pastries"),
                menuItemsAr = listOf("قوزي غنم محشي", "محطة باستا مباشرة", "طبق سوشي", "نافورة شوكولاتة", "معجنات مشكلة"),
                imageUrl = "https://images.unsplash.com/photo-1574966739987-65e38a0b080f"
            ),
            CateringPackage(
                id = "cat_4",
                providerName = "Isfahani Restaurant",
                providerNameAr = "مطعم أصفهاني",
                packageName = "Persian Delight",
                packageNameAr = "المذاق الفارسي",
                description = "Authentic Persian cuisine with premium service.",
                descriptionAr = "مأكولات فارسية أصيلة مع خدمة متميزة.",
                pricePerPerson = 8.0,
                tier = CateringTier.VERY_EXCELLENT,
                menuItems = listOf("Chelo Kabab", "Ghormeh Sabzi", "Saffron Rice", "Persian Tea", "Baklava"),
                menuItemsAr = listOf("تشيلو كباب", "قورمة سبزي", "رز بالزعفران", "شاي إيراني", "بقلاوة"),
                imageUrl = "https://images.unsplash.com/photo-1594998893017-3614795c2563"
            ),

            // Tier 3: Excellent (الممتاز - Luxury)
            CateringPackage(
                id = "cat_5",
                providerName = "Ritz-Carlton Events",
                providerNameAr = "ريتز كارلتون للمناسبات",
                packageName = "Diamond Wedding",
                packageNameAr = "الزفاف الماسي",
                description = "The epitome of luxury and fine dining.",
                descriptionAr = "قمة الفخامة وتجربة طعام راقية.",
                pricePerPerson = 18.0,
                tier = CateringTier.EXCELLENT,
                menuItems = listOf("Lobster Thermidor", "Wagyu Beef Sliders", "Caviar Station", "French Macarons", "Mocktail Bar"),
                menuItemsAr = listOf("لوبستر ثيرميدور", "سلايدر واغيو", "محطة كافيار", "ماكرون فرنسي", "بار موكتيل"),
                imageUrl = "https://images.unsplash.com/photo-1533777857889-4be7c70b33f7"
            ),
            CateringPackage(
                id = "cat_6",
                providerName = "Four Seasons Catering",
                providerNameAr = "الفورسيزونز للضيافة",
                packageName = "Elite Experience",
                packageNameAr = "تجربة النخبة",
                description = "World-class culinary artistry for your special day.",
                descriptionAr = "فنون طهي عالمية ليومك المميز.",
                pricePerPerson = 22.0,
                tier = CateringTier.EXCELLENT,
                menuItems = listOf("Seafood Tower", "Truffle Risotto", "Hand-crafted Chocolates", "Signature Welcome Drinks"),
                menuItemsAr = listOf("برج المأكولات البحرية", "ريزوتو الكمأة", "شوكولاتة يدوية الصنع", "مشروبات ترحيبية خاصة"),
                imageUrl = "https://images.unsplash.com/photo-1519225468359-2f2dbce190aa"
            )
        )
    }
}
