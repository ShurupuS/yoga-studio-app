package com.yoga.app.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "subscriptions")
data class Subscription(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    val client: Client,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_type_id", nullable = false)
    val subscriptionType: SubscriptionType,
    
    @Column(nullable = false)
    val startDate: LocalDateTime,
    
    @Column(nullable = false)
    val endDate: LocalDateTime,
    
    @Column(nullable = false)
    val price: BigDecimal,
    
    @Column(nullable = false)
    val isActive: Boolean = true,
    
    @Column
    val notes: String? = null,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column
    val updatedAt: LocalDateTime? = null
)

@Entity
@Table(name = "subscription_types")
data class SubscriptionType(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    val name: String,
    
    @Column
    val description: String? = null,
    
    @Column(nullable = false)
    val price: BigDecimal,
    
    @Column(nullable = false)
    val durationDays: Int,
    
    @Column(nullable = false)
    val maxClasses: Int? = null, // null = unlimited
    
    @Column(nullable = false)
    val isActive: Boolean = true,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column
    val updatedAt: LocalDateTime? = null
)


