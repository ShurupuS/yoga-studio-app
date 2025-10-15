package com.yoga.app.dto

import com.yoga.app.entity.UserRole
import java.time.LocalDateTime

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

data class AuthResponse(
    val accessToken: String,
    val user: UserDto
)

data class UserDto(
    val id: Long,
    val email: String,
    val name: String,
    val role: UserRole,
    val isActive: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(user: com.yoga.app.entity.User): UserDto {
            return UserDto(
                id = user.id,
                email = user.email,
                name = user.name,
                role = user.role,
                isActive = user.isActive,
                createdAt = user.createdAt
            )
        }
    }
}


