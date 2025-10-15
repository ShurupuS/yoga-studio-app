package com.yoga.app.service

import com.yoga.app.dto.*
import com.yoga.app.entity.Subscription
import com.yoga.app.entity.SubscriptionType
import com.yoga.app.repository.ClientRepository
import com.yoga.app.repository.SubscriptionRepository
import com.yoga.app.repository.SubscriptionTypeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class SubscriptionService(
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriptionTypeRepository: SubscriptionTypeRepository,
    private val clientRepository: ClientRepository
) {
    
    // Subscription Type Management
    fun getAllSubscriptionTypes(): List<SubscriptionTypeDto> {
        return subscriptionTypeRepository.findByIsActiveTrueOrderByNameAsc()
            .map { SubscriptionTypeDto.from(it) }
    }
    
    fun getSubscriptionTypeById(id: Long): SubscriptionTypeDto {
        val subscriptionType = subscriptionTypeRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Subscription type not found with id: $id") }
        return SubscriptionTypeDto.from(subscriptionType)
    }
    
    fun createSubscriptionType(request: CreateSubscriptionTypeRequest): SubscriptionTypeDto {
        val subscriptionType = SubscriptionType(
            name = request.name,
            description = request.description,
            price = request.price,
            durationDays = request.durationDays,
            maxClasses = request.maxClasses
        )
        
        val savedType = subscriptionTypeRepository.save(subscriptionType)
        return SubscriptionTypeDto.from(savedType)
    }
    
    fun updateSubscriptionType(id: Long, request: UpdateSubscriptionTypeRequest): SubscriptionTypeDto {
        val subscriptionType = subscriptionTypeRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Subscription type not found with id: $id") }
        
        val updatedType = subscriptionType.copy(
            name = request.name ?: subscriptionType.name,
            description = request.description ?: subscriptionType.description,
            price = request.price ?: subscriptionType.price,
            durationDays = request.durationDays ?: subscriptionType.durationDays,
            maxClasses = request.maxClasses ?: subscriptionType.maxClasses,
            isActive = request.isActive ?: subscriptionType.isActive,
            updatedAt = LocalDateTime.now()
        )
        
        val savedType = subscriptionTypeRepository.save(updatedType)
        return SubscriptionTypeDto.from(savedType)
    }
    
    fun deleteSubscriptionType(id: Long) {
        val subscriptionType = subscriptionTypeRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Subscription type not found with id: $id") }
        
        val deactivatedType = subscriptionType.copy(
            isActive = false,
            updatedAt = LocalDateTime.now()
        )
        
        subscriptionTypeRepository.save(deactivatedType)
    }
    
    // Subscription Management
    fun getAllSubscriptions(pageable: Pageable): Page<SubscriptionDto> {
        return subscriptionRepository.findByIsActiveTrue(pageable)
            .map { SubscriptionDto.from(it) }
    }
    
    fun getSubscriptionsByClient(clientId: Long, pageable: Pageable): Page<SubscriptionDto> {
        return subscriptionRepository.findByClientId(clientId, pageable)
            .map { SubscriptionDto.from(it) }
    }
    
    fun getSubscriptionById(id: Long): SubscriptionDto {
        val subscription = subscriptionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Subscription not found with id: $id") }
        return SubscriptionDto.from(subscription)
    }
    
    fun createSubscription(request: CreateSubscriptionRequest): SubscriptionDto {
        val client = clientRepository.findById(request.clientId)
            .orElseThrow { IllegalArgumentException("Client not found with id: ${request.clientId}") }
        
        val subscriptionType = subscriptionTypeRepository.findById(request.subscriptionTypeId)
            .orElseThrow { IllegalArgumentException("Subscription type not found with id: ${request.subscriptionTypeId}") }
        
        val endDate = request.startDate.plusDays(subscriptionType.durationDays.toLong())
        
        val subscription = Subscription(
            client = client,
            subscriptionType = subscriptionType,
            startDate = request.startDate,
            endDate = endDate,
            price = request.price,
            notes = request.notes
        )
        
        val savedSubscription = subscriptionRepository.save(subscription)
        return SubscriptionDto.from(savedSubscription)
    }
    
    fun deactivateSubscription(id: Long) {
        val subscription = subscriptionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Subscription not found with id: $id") }
        
        val deactivatedSubscription = subscription.copy(
            isActive = false,
            updatedAt = LocalDateTime.now()
        )
        
        subscriptionRepository.save(deactivatedSubscription)
    }
}


