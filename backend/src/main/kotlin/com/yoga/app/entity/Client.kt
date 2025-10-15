package com.yoga.app.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "clients")
data class Client(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    val name: String,
    
    @Column(unique = true)
    val phone: String? = null,
    
    @Column(unique = true)
    val email: String? = null,
    
    @Column
    val birthDate: LocalDate? = null,
    
    @Column
    val notes: String? = null,
    
    @Column(nullable = false)
    val isActive: Boolean = true,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column
    val updatedAt: LocalDateTime? = null,
    
    @OneToMany(mappedBy = "client", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val subscriptions: List<Subscription> = emptyList(),
    
    @OneToMany(mappedBy = "client", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val attendances: List<Attendance> = emptyList()
)


