package com.example.qa3at.data.repository

import com.example.qa3at.data.remote.ApiService
import com.example.qa3at.domain.model.City
import com.example.qa3at.domain.model.TimeSlot
import com.example.qa3at.domain.model.Venue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VenueRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun searchVenues(
        query: String? = null,
        city: String? = null,
        type: String? = null,
        date: String? = null,
        guests: Int? = null,
        slotId: String? = null,
        sortBy: String? = null,
        page: Int = 1,
        limit: Int = 10
    ): Result<Pair<List<Venue>, Int>> {
        return try {
            val response = apiService.searchVenues(
                query = query,
                city = city,
                type = type,
                date = date,
                guests = guests,
                slotId = slotId,
                sortBy = sortBy,
                page = page,
                limit = limit
            )

            if (response.isSuccessful) {
                val body = response.body()!!
                val venues = body.data.map { it.toDomain() }
                Result.Success(Pair(venues, body.meta.totalPages))
            } else {
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getVenueDetails(id: String): Result<Venue> {
        return try {
            val response = apiService.getVenueDetails(id)

            if (response.isSuccessful) {
                Result.Success(response.body()!!.toDomain())
            } else {
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getCities(): Result<List<City>> {
        // Return all Bahraini cities with venue counts
        // Total Venues: 12
        // Manama: 9, Sitra: 1, A'ali: 1, Bani Jamra: 1
        return Result.Success(
            listOf(
                City("all", "All (12)", "الكل (12)"),
                City("Manama", "Manama (9)", "المنامة (9)"),
                City("Al Muharraq", "Al Muḩarraq (0)", "المحرق (0)"),
                City("Madinat Hamad", "Madīnat Ḩamad (0)", "مدينة حمد (0)"),
                City("Jidd Hafs", "Jidd Ḩafş (0)", "جد حفص (0)"),
                City("Madinat Isa", "Madīnat ‘Īsá (0)", "مدينة عيسى (0)"),
                City("Sitra", "Sitrah (1)", "سترة (1)"),
                City("Bani Jamra", "Bani Jamra (1)", "بني جمرة (1)"),
                City("Aali", "‘Ālī (1)", "عالي (1)"),
                City("Riffa", "Ar Rifā‘ (0)", "الرفاع (0)"),
                City("Halat an Naim", "Ḩālat an Na‘īm (0)", "حالة النعيم (0)"),
                City("Kaflan", "Kaflān (0)", "كفلان (0)"),
                City("Karbabad", "Karbābād (0)", "كرباباد (0)"),
                City("Jabalat Habashi", "Jabalat Ḩabashī (0)", "جبلة حبشي (0)"),
                City("Ayn ad Dar", "‘Ayn ad Dār (0)", "عين الدار (0)"),
                City("Hillat Abd as Salih", "Ḩillat ‘Abd aş Şāliḩ (0)", "حلة عبد الصالح (0)"),
                City("Al Muqsha", "Al Muqshā‘ (0)", "المقشع (0)"),
                City("Mani", "Manī (0)", "مني (0)"),
                City("Al Muwaylighah", "Al Muwaylighah (0)", "المويلغة (0)"),
                City("Al Busaytin", "Al Busaytīn (0)", "البسيتين (0)"),
                City("Rayya", "Rayyā (0)", "ريا (0)"),
                City("Arad", "‘Arād (0)", "عراد (0)"),
                City("Qalali", "Qalālī (0)", "قلالي (0)"),
                City("Samahij", "Samāhīj (0)", "سماهيج (0)"),
                City("Ad Dayr", "Ad Dayr (0)", "الدير (0)"),
                City("Halat as Sultah", "Ḩālat as Sulţah (0)", "حالة السلطة (0)"),
                City("Az Zimmah", "Az Zimmah (0)", "الزمة (0)"),
                City("Halat al Khulayfat", "Ḩālat al Khulayfāt (0)", "حالة الخليفات (0)"),
                City("Al Hadd", "Al Ḩadd (0)", "الحد (0)"),
                City("Umm ash Shajar", "Umm ash Shajar (0)", "أم الشجر (0)"),
                City("Al Hujayr", "Al Hujayr (0)", "الحجر (0)"),
                City("An Nuwaydirat", "An Nuwaydirāt (0)", "النويدرات (0)"),
                City("Al Jubaylat", "Al Jubaylāt (0)", "الجبيلات (0)"),
                City("Al Qaryah", "Al Qaryah (0)", "القرية (0)"),
                City("Halah", "Ḩālah (0)", "حالة (0)"),
                City("Marquban", "Marqūbān (0)", "مرقوبان (0)"),
                City("Sufalah", "Sufālah (0)", "سفالة (0)"),
                City("Halat Umm al Bayd", "Ḩālat Umm al Bayḑ (0)", "حالة أم البيض (0)"),
                City("Al Akr", "Al ‘Akr (0)", "العكر (0)"),
                City("Al Maamir", "Al Ma‘āmīr (0)", "المعامير (0)"),
                City("Jidd Ali", "Jidd ‘Alī (0)", "جد علي (0)"),
                City("Jurdab", "Jurdāb (0)", "جرداب (0)"),
                City("Sanad", "Sanad (0)", "سند (0)"),
                City("Al Kharijiyah", "Al Khārijīyah (0)", "الخارجية (0)"),
                City("Kawrah", "Kawrah (0)", "كورة (0)"),
                City("Mahazzah", "Mahazzah (0)", "مهزة (0)"),
                City("Wadiyan", "Wādiyān (0)", "واديان (0)"),
                City("Salmabad", "Salmābād (0)", "سلماباد (0)"),
                City("Tubli", "Tūblī (0)", "توبلي (0)"),
                City("Al Qadam", "Al Qadam (0)", "القدم (0)"),
                City("Jamalah", "Jamālah (0)", "جمالة (0)")
            )
        )
    }

    suspend fun getTimeSlots(): Result<List<TimeSlot>> {
        return try {
            val response = apiService.getTimeSlots()

            if (response.isSuccessful) {
                Result.Success(response.body()!!.map { it.toDomain() })
            } else {
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}
