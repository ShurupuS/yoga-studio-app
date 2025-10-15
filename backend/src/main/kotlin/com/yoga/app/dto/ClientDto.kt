package com.yoga.app.dto

import com.yoga.app.entity.Client
import java.time.LocalDate
import java.time.LocalDateTime

data class ClientDto(
    val id: Long,
    val name: String,
    val phone: String?,
    val email: String?,
    val birthDate: LocalDate?,
    val notes: String?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val subscriptionCount: Int = 0
) {
    companion object {
        fun from(client: Client): ClientDto {
            return ClientDto(
                id = client.id,
                name = client.name,
                phone = client.phone,
                email = client.email,
                birthDate = client.birthDate,
                notes = client.notes,
                isActive = client.isActive,
                createdAt = client.createdAt,
                subscriptionCount = client.subscriptions.size
            )
        }
    }
}

data class CreateClientRequest(
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    val birthDate: LocalDate? = null,
    val notes: String? = null
)

data class UpdateClientRequest(
    val name: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val birthDate: LocalDate? = null,
    val notes: String? = null,
    val isActive: Boolean? = null
)


