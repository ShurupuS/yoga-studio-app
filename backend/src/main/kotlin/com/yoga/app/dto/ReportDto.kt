package com.yoga.app.dto

import com.yoga.app.entity.AttendanceStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class AttendanceReportDto(
    val totalClasses: Int,
    val totalAttendances: Int,
    val presentCount: Int,
    val absentCount: Int,
    val lateCount: Int,
    val cancelledCount: Int,
    val attendanceRate: Double
)

data class RevenueReportDto(
    val totalRevenue: BigDecimal,
    val activeSubscriptions: Int,
    val expiredSubscriptions: Int,
    val averageSubscriptionValue: BigDecimal,
    val monthlyRevenue: List<MonthlyRevenueDto>
)

data class MonthlyRevenueDto(
    val month: String,
    val year: Int,
    val revenue: BigDecimal,
    val subscriptionCount: Int
)

data class ClientReportDto(
    val totalClients: Int,
    val activeClients: Int,
    val newClientsThisMonth: Int,
    val clientsWithActiveSubscriptions: Int,
    val averageClassesPerClient: Double
)

data class ClassReportDto(
    val totalClasses: Int,
    val completedClasses: Int,
    val cancelledClasses: Int,
    val averageAttendance: Double,
    val mostPopularClass: String?,
    val leastPopularClass: String?
)

data class ReportRequest(
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val clientId: Long? = null,
    val classId: Long? = null
)


