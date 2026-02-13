package com.example.qa3at.data.repository

import com.example.qa3at.data.remote.ApiService
import com.example.qa3at.data.remote.dto.CreateBookingRequest
import com.example.qa3at.domain.model.Booking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun createBooking(
        venueId: String,
        slotId: String,
        date: String,
        guestCount: Int,
        packageIds: List<String>? = null,
        addonIds: List<String>? = null,
        notes: String? = null
    ): Result<Booking> {
        return try {
            val response = apiService.createBooking(
                CreateBookingRequest(
                    venueId = venueId,
                    slotId = slotId,
                    date = date,
                    guestCount = guestCount,
                    packageIds = packageIds,
                    addonIds = addonIds,
                    notes = notes
                )
            )

            if (response.isSuccessful) {
                Result.Success(response.body()!!.toDomain())
            } else {
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getBookings(): Result<List<Booking>> {
        return try {
            val response = apiService.getBookings()

            if (response.isSuccessful) {
                Result.Success(response.body()!!.map { it.toDomain() })
            } else {
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getBookingDetails(id: String): Result<Booking> {
        return try {
            val response = apiService.getBookingDetails(id)

            if (response.isSuccessful) {
                Result.Success(response.body()!!.toDomain())
            } else {
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun cancelBooking(id: String): Result<Booking> {
        return try {
            val response = apiService.cancelBooking(id)

            if (response.isSuccessful) {
                Result.Success(response.body()!!.toDomain())
            } else {
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}
