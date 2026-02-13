package com.example.qa3at.data.remote.dto

import com.example.qa3at.domain.model.User
import com.example.qa3at.domain.model.UserRole

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val phone: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val user: UserDto,
    val accessToken: String
)

data class UserDto(
    val id: String,
    val email: String,
    val name: String,
    val phone: String? = null,
    val role: String = "USER"
) {
    fun toDomain(): User = User(
        id = id,
        email = email,
        name = name,
        phone = phone,
        role = when (role) {
            "ADMIN" -> UserRole.ADMIN
            "VENDOR" -> UserRole.VENDOR
            else -> UserRole.USER
        }
    )
}
