package com.yoga.app.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@Table(name = "classes")
data class Class(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    val name: String,
    
    @Column
    val description: String? = null,
    
    @Column(nullable = false)
    val startTime: LocalDateTime,
    
    @Column(nullable = false)
    val endTime: LocalDateTime,
    
    @Column(nullable = false)
    val maxCapacity: Int,
    
    @Column(nullable = false)
    val currentCapacity: Int = 0,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: ClassStatus = ClassStatus.SCHEDULED,
    
    @Column
    val notes: String? = null,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column
    val updatedAt: LocalDateTime? = null,
    
    @OneToMany(mappedBy = "class", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val attendances: List<Attendance> = emptyList()
)

@Entity
@Table(name = "attendances")
data class Attendance(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    val client: Client,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    val class: Class,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    
    @Column
    val notes: String? = null,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class ClassStatus {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    LATE,
    CANCELLED
}


