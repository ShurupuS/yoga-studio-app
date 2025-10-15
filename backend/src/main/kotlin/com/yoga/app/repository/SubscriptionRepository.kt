package com.yoga.app.repository

import com.yoga.app.entity.Subscription
import com.yoga.app.entity.SubscriptionType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SubscriptionRepository : JpaRepository<Subscription, Long> {
    fun findByClientId(clientId: Long, pageable: Pageable): Page<Subscription>
    fun findByClientIdAndIsActiveTrue(clientId: Long): List<Subscription>
    fun findByIsActiveTrue(pageable: Pageable): Page<Subscription>
    
    @Query("SELECT s FROM Subscription s WHERE s.client.id = :clientId AND " +
            "s.startDate <= :date AND s.endDate >= :date AND s.isActive = true")
    fun findActiveSubscriptionsByClientAndDate(
        @Param("clientId") clientId: Long,
        @Param("date") date: LocalDateTime
    ): List<Subscription>
}

@Repository
interface SubscriptionTypeRepository : JpaRepository<SubscriptionType, Long> {
    fun findByIsActiveTrueOrderByNameAsc(): List<SubscriptionType>
    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<SubscriptionType>
}


