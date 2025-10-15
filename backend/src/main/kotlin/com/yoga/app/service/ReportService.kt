package com.yoga.app.service

import com.yoga.app.dto.*
import com.yoga.app.entity.AttendanceStatus
import com.yoga.app.repository.AttendanceRepository
import com.yoga.app.repository.ClientRepository
import com.yoga.app.repository.ClassRepository
import com.yoga.app.repository.SubscriptionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
@Transactional(readOnly = true)
class ReportService(
    private val attendanceRepository: AttendanceRepository,
    private val clientRepository: ClientRepository,
    private val classRepository: ClassRepository,
    private val subscriptionRepository: SubscriptionRepository
) {
    
    fun getAttendanceReport(request: ReportRequest): AttendanceReportDto {
        val attendances = if (request.clientId != null) {
            attendanceRepository.findByClientId(request.clientId, org.springframework.data.domain.Pageable.unpaged())
                .content
                .filter { it.class.startTime >= request.startDate && it.class.startTime <= request.endDate }
        } else {
            attendanceRepository.findAttendancesInDateRange(request.startDate, request.endDate)
        }
        
        val totalClasses = if (request.classId != null) {
            1
        } else {
            classRepository.findClassesInDateRange(request.startDate, request.endDate).size
        }
        
        val totalAttendances = attendances.size
        val presentCount = attendances.count { it.status == AttendanceStatus.PRESENT }
        val absentCount = attendances.count { it.status == AttendanceStatus.ABSENT }
        val lateCount = attendances.count { it.status == AttendanceStatus.LATE }
        val cancelledCount = attendances.count { it.status == AttendanceStatus.CANCELLED }
        
        val attendanceRate = if (totalClasses > 0) {
            (presentCount.toDouble() / totalClasses * 100).let { BigDecimal(it).setScale(2, RoundingMode.HALF_UP).toDouble() }
        } else 0.0
        
        return AttendanceReportDto(
            totalClasses = totalClasses,
            totalAttendances = totalAttendances,
            presentCount = presentCount,
            absentCount = absentCount,
            lateCount = lateCount,
            cancelledCount = cancelledCount,
            attendanceRate = attendanceRate
        )
    }
    
    fun getRevenueReport(request: ReportRequest): RevenueReportDto {
        val subscriptions = subscriptionRepository.findByIsActiveTrue(org.springframework.data.domain.Pageable.unpaged()).content
        val filteredSubscriptions = subscriptions.filter { 
            it.startDate >= request.startDate && it.endDate <= request.endDate 
        }
        
        val totalRevenue = filteredSubscriptions.sumOf { it.price }
        val activeSubscriptions = subscriptions.count { it.isActive }
        val expiredSubscriptions = subscriptions.count { !it.isActive }
        val averageSubscriptionValue = if (filteredSubscriptions.isNotEmpty()) {
            totalRevenue.divide(BigDecimal(filteredSubscriptions.size), 2, RoundingMode.HALF_UP)
        } else BigDecimal.ZERO
        
        val monthlyRevenue = generateMonthlyRevenue(request.startDate, request.endDate, filteredSubscriptions)
        
        return RevenueReportDto(
            totalRevenue = totalRevenue,
            activeSubscriptions = activeSubscriptions,
            expiredSubscriptions = expiredSubscriptions,
            averageSubscriptionValue = averageSubscriptionValue,
            monthlyRevenue = monthlyRevenue
        )
    }
    
    fun getClientReport(request: ReportRequest): ClientReportDto {
        val allClients = clientRepository.findAll()
        val activeClients = allClients.count { it.isActive }
        val newClientsThisMonth = allClients.count { 
            it.createdAt.isAfter(request.startDate) && it.createdAt.isBefore(request.endDate) 
        }
        
        val clientsWithActiveSubscriptions = allClients.count { client ->
            client.subscriptions.any { it.isActive }
        }
        
        val attendances = attendanceRepository.findAttendancesInDateRange(request.startDate, request.endDate)
        val averageClassesPerClient = if (activeClients > 0) {
            attendances.size.toDouble() / activeClients
        } else 0.0
        
        return ClientReportDto(
            totalClients = allClients.size,
            activeClients = activeClients,
            newClientsThisMonth = newClientsThisMonth,
            clientsWithActiveSubscriptions = clientsWithActiveSubscriptions,
            averageClassesPerClient = BigDecimal(averageClassesPerClient).setScale(2, RoundingMode.HALF_UP).toDouble()
        )
    }
    
    fun getClassReport(request: ReportRequest): ClassReportDto {
        val classes = classRepository.findClassesInDateRange(request.startDate, request.endDate)
        val totalClasses = classes.size
        val completedClasses = classes.count { it.status == com.yoga.app.entity.ClassStatus.COMPLETED }
        val cancelledClasses = classes.count { it.status == com.yoga.app.entity.ClassStatus.CANCELLED }
        
        val attendances = attendanceRepository.findAttendancesInDateRange(request.startDate, request.endDate)
        val averageAttendance = if (totalClasses > 0) {
            attendances.size.toDouble() / totalClasses
        } else 0.0
        
        val classAttendanceMap = classes.associateWith { clazz ->
            attendances.count { it.class.id == clazz.id && it.status == AttendanceStatus.PRESENT }
        }
        
        val mostPopularClass = classAttendanceMap.maxByOrNull { it.value }?.key?.name
        val leastPopularClass = classAttendanceMap.minByOrNull { it.value }?.key?.name
        
        return ClassReportDto(
            totalClasses = totalClasses,
            completedClasses = completedClasses,
            cancelledClasses = cancelledClasses,
            averageAttendance = BigDecimal(averageAttendance).setScale(2, RoundingMode.HALF_UP).toDouble(),
            mostPopularClass = mostPopularClass,
            leastPopularClass = leastPopularClass
        )
    }
    
    private fun generateMonthlyRevenue(startDate: LocalDateTime, endDate: LocalDateTime, subscriptions: List<com.yoga.app.entity.Subscription>): List<MonthlyRevenueDto> {
        val monthlyData = mutableMapOf<String, Pair<BigDecimal, Int>>()
        
        subscriptions.forEach { subscription ->
            val monthKey = subscription.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM"))
            val current = monthlyData[monthKey] ?: Pair(BigDecimal.ZERO, 0)
            monthlyData[monthKey] = Pair(
                current.first.add(subscription.price),
                current.second + 1
            )
        }
        
        return monthlyData.map { (month, data) ->
            val parts = month.split("-")
            MonthlyRevenueDto(
                month = parts[1],
                year = parts[0].toInt(),
                revenue = data.first,
                subscriptionCount = data.second
            )
        }.sortedWith(compareBy({ it.year }, { it.month.toInt() }))
    }
}


