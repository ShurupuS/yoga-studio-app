package com.yoga.app.repository

import com.yoga.app.entity.Attendance
import com.yoga.app.entity.AttendanceStatus
import com.yoga.app.entity.Class
import com.yoga.app.entity.ClassStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ClassRepository : JpaRepository<Class, Long> {
    fun findByStartTimeBetween(start: LocalDateTime, end: LocalDateTime, pageable: Pageable): Page<Class>
    fun findByStartTimeBetweenAndStatus(start: LocalDateTime, end: LocalDateTime, status: ClassStatus, pageable: Pageable): Page<Class>
    fun findByStatusOrderByStartTimeAsc(status: ClassStatus, pageable: Pageable): Page<Class>
    
    @Query("SELECT c FROM Class c WHERE c.startTime >= :startDate AND c.startTime <= :endDate " +
            "ORDER BY c.startTime ASC")
    fun findClassesInDateRange(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Class>
}

@Repository
interface AttendanceRepository : JpaRepository<Attendance, Long> {
    fun findByClientId(clientId: Long, pageable: Pageable): Page<Attendance>
    fun findByClassId(classId: Long): List<Attendance>
    fun findByClientIdAndClassId(clientId: Long, classId: Long): Attendance?
    fun findByStatus(status: AttendanceStatus, pageable: Pageable): Page<Attendance>
    
    @Query("SELECT a FROM Attendance a WHERE a.class.startTime >= :startDate AND a.class.startTime <= :endDate")
    fun findAttendancesInDateRange(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Attendance>
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.client.id = :clientId AND a.status = :status " +
            "AND a.class.startTime >= :startDate AND a.class.startTime <= :endDate")
    fun countAttendancesByClientAndStatusInDateRange(
        @Param("clientId") clientId: Long,
        @Param("status") status: AttendanceStatus,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): Long
}


