package com.yoga.app.dto

import com.yoga.app.entity.Subscription
import com.yoga.app.entity.SubscriptionType
import java.math.BigDecimal
import java.time.LocalDateTime

data class SubscriptionDto(
    val id: Long,
    val clientId: Long,
    val clientName: String,
    val subscriptionTypeId: Long,
    val subscriptionTypeName: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val price: BigDecimal,
    val isActive: Boolean,
    val notes: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(subscription: Subscription): SubscriptionDto {
            return SubscriptionDto(
                id = subscription.id,
                clientId = subscription.client.id,
                clientName = subscription.client.name,
                subscriptionTypeId = subscription.subscriptionType.id,
                subscriptionTypeName = subscription.subscriptionType.name,
                startDate = subscription.startDate,
                endDate = subscription.endDate,
                price = subscription.price,
                isActive = subscription.isActive,
                notes = subscription.notes,
                createdAt = subscription.createdAt
            )
        }
    }
}

data class SubscriptionTypeDto(
    val id: Long,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val durationDays: Int,
    val maxClasses: Int?,
    val isActive: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(subscriptionType: SubscriptionType): SubscriptionTypeDto {
            return SubscriptionTypeDto(
                id = subscriptionType.id,
                name = subscriptionType.name,
                description = subscriptionType.description,
                price = subscriptionType.price,
                durationDays = subscriptionType.durationDays,
                maxClasses = subscriptionType.maxClasses,
                isActive = subscriptionType.isActive,
                createdAt = subscriptionType.createdAt
            )
        }
    }
}

data class CreateSubscriptionRequest(
    val clientId: Long,
    val subscriptionTypeId: Long,
    val startDate: LocalDateTime,
    val price: BigDecimal,
    val notes: String? = null
)

data class CreateSubscriptionTypeRequest(
    val name: String,
    val description: String? = null,
    val price: BigDecimal,
    val durationDays: Int,
    val maxClasses: Int? = null
)

data class UpdateSubscriptionTypeRequest(
    val name: String? = null,
    val description: String? = null,
    val price: BigDecimal? = null,
    val durationDays: Int? = null,
    val maxClasses: Int? = null,
    val isActive: Boolean? = null
)


