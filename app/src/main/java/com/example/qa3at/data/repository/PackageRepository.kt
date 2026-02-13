package com.example.qa3at.data.repository

import com.example.qa3at.data.remote.ApiService
import com.example.qa3at.domain.model.Addon
import com.example.qa3at.domain.model.Package
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PackageRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getPackages(category: String? = null): Result<List<Package>> {
        return try {
            val response = apiService.getPackages(category)

            if (response.isSuccessful) {
                Result.Success(response.body()!!.map { it.toDomain() })
            } else {
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getAddons(category: String? = null): Result<List<Addon>> {
        return try {
            val response = apiService.getAddons(category)

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
