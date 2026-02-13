package com.example.qa3at.data.remote

import com.example.qa3at.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("auth/profile")
    suspend fun getProfile(): Response<UserDto>

    // Venues
    @GET("venues")
    suspend fun searchVenues(
        @Query("query") query: String? = null,
        @Query("city") city: String? = null,
        @Query("type") type: String? = null,
        @Query("date") date: String? = null,
        @Query("guests") guests: Int? = null,
        @Query("slotId") slotId: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<VenueListResponse>

    @GET("venues/{id}")
    suspend fun getVenueDetails(@Path("id") id: String): Response<VenueDto>

    @GET("venues/cities")
    suspend fun getCities(): Response<List<CityDto>>

    @GET("venues/{id}/availability")
    suspend fun getVenueAvailability(
        @Path("id") id: String,
        @Query("date") date: String
    ): Response<List<AvailabilityDto>>

    // Packages
    @GET("packages")
    suspend fun getPackages(@Query("category") category: String? = null): Response<List<PackageDto>>

    @GET("packages/addons")
    suspend fun getAddons(@Query("category") category: String? = null): Response<List<AddonDto>>

    @GET("packages/time-slots")
    suspend fun getTimeSlots(): Response<List<TimeSlotDto>>

    // Bookings
    @POST("bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): Response<BookingDto>

    @GET("bookings")
    suspend fun getBookings(): Response<List<BookingDto>>

    @GET("bookings/{id}")
    suspend fun getBookingDetails(@Path("id") id: String): Response<BookingDto>

    @PATCH("bookings/{id}/cancel")
    suspend fun cancelBooking(@Path("id") id: String): Response<BookingDto>

    // Assistant
    @POST("assistant/chat")
    suspend fun chat(@Body request: ChatRequest): Response<ChatResponse>
}
