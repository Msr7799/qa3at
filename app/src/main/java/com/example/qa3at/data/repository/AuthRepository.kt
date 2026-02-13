package com.example.qa3at.data.repository

import com.example.qa3at.data.local.TokenManager
import com.example.qa3at.data.remote.ApiService
import com.example.qa3at.data.remote.dto.LoginRequest
import com.example.qa3at.data.remote.dto.RegisterRequest
import com.example.qa3at.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    suspend fun register(
        email: String,
        password: String,
        name: String,
        phone: String?
    ): Result<User> {
        return try {
            val response = apiService.register(
                RegisterRequest(
                    email = email,
                    password = password,
                    name = name,
                    phone = phone
                )
            )

            if (response.isSuccessful) {
                val body = response.body()!!
                tokenManager.saveToken(body.accessToken)
                tokenManager.saveUserInfo(body.user.id, body.user.name, body.user.email)
                Result.Success(body.user.toDomain())
            } else {
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = apiService.login(LoginRequest(email, password))

            if (response.isSuccessful) {
                val body = response.body()!!
                tokenManager.saveToken(body.accessToken)
                tokenManager.saveUserInfo(body.user.id, body.user.name, body.user.email)
                Result.Success(body.user.toDomain())
            } else {
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getProfile(): Result<User> {
        return try {
            val response = apiService.getProfile()

            if (response.isSuccessful) {
                Result.Success(response.body()!!.toDomain())
            } else {
                Result.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun logout() {
        tokenManager.clearToken()
        tokenManager.clearUserInfo()
    }

    fun isLoggedIn(): Flow<Boolean> = tokenManager.isLoggedIn()
}
