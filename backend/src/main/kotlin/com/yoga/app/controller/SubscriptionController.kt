package com.yoga.app.controller

import com.yoga.app.dto.*
import com.yoga.app.service.SubscriptionService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = ["*"])
class SubscriptionController(
    private val subscriptionService: SubscriptionService
) {
    
    // Subscription Type Management
    @GetMapping("/types")
    fun getAllSubscriptionTypes(): ResponseEntity<List<SubscriptionTypeDto>> {
        val types = subscriptionService.getAllSubscriptionTypes()
        return ResponseEntity.ok(types)
    }
    
    @GetMapping("/types/{id}")
    fun getSubscriptionTypeById(@PathVariable id: Long): ResponseEntity<SubscriptionTypeDto> {
        val type = subscriptionService.getSubscriptionTypeById(id)
        return ResponseEntity.ok(type)
    }
    
    @PostMapping("/types")
    fun createSubscriptionType(@Valid @RequestBody request: CreateSubscriptionTypeRequest): ResponseEntity<SubscriptionTypeDto> {
        val type = subscriptionService.createSubscriptionType(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(type)
    }
    
    @PutMapping("/types/{id}")
    fun updateSubscriptionType(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateSubscriptionTypeRequest
    ): ResponseEntity<SubscriptionTypeDto> {
        val type = subscriptionService.updateSubscriptionType(id, request)
        return ResponseEntity.ok(type)
    }
    
    @DeleteMapping("/types/{id}")
    fun deleteSubscriptionType(@PathVariable id: Long): ResponseEntity<Void> {
        subscriptionService.deleteSubscriptionType(id)
        return ResponseEntity.noContent().build()
    }
    
    // Subscription Management
    @GetMapping
    fun getAllSubscriptions(pageable: Pageable): ResponseEntity<Page<SubscriptionDto>> {
        val subscriptions = subscriptionService.getAllSubscriptions(pageable)
        return ResponseEntity.ok(subscriptions)
    }
    
    @GetMapping("/client/{clientId}")
    fun getSubscriptionsByClient(@PathVariable clientId: Long, pageable: Pageable): ResponseEntity<Page<SubscriptionDto>> {
        val subscriptions = subscriptionService.getSubscriptionsByClient(clientId, pageable)
        return ResponseEntity.ok(subscriptions)
    }
    
    @GetMapping("/{id}")
    fun getSubscriptionById(@PathVariable id: Long): ResponseEntity<SubscriptionDto> {
        val subscription = subscriptionService.getSubscriptionById(id)
        return ResponseEntity.ok(subscription)
    }
    
    @PostMapping
    fun createSubscription(@Valid @RequestBody request: CreateSubscriptionRequest): ResponseEntity<SubscriptionDto> {
        val subscription = subscriptionService.createSubscription(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription)
    }
    
    @DeleteMapping("/{id}")
    fun deactivateSubscription(@PathVariable id: Long): ResponseEntity<Void> {
        subscriptionService.deactivateSubscription(id)
        return ResponseEntity.noContent().build()
    }
}


