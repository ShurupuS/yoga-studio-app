package com.yoga.app.controller

import com.yoga.app.dto.*
import com.yoga.app.service.ReportService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = ["*"])
class ReportController(
    private val reportService: ReportService
) {
    
    @GetMapping("/attendance")
    fun getAttendanceReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime,
        @RequestParam(required = false) clientId: Long?
    ): ResponseEntity<AttendanceReportDto> {
        val request = ReportRequest(startDate, endDate, clientId, null)
        val report = reportService.getAttendanceReport(request)
        return ResponseEntity.ok(report)
    }
    
    @GetMapping("/revenue")
    fun getRevenueReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime
    ): ResponseEntity<RevenueReportDto> {
        val request = ReportRequest(startDate, endDate, null, null)
        val report = reportService.getRevenueReport(request)
        return ResponseEntity.ok(report)
    }
    
    @GetMapping("/clients")
    fun getClientReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime
    ): ResponseEntity<ClientReportDto> {
        val request = ReportRequest(startDate, endDate, null, null)
        val report = reportService.getClientReport(request)
        return ResponseEntity.ok(report)
    }
    
    @GetMapping("/classes")
    fun getClassReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime
    ): ResponseEntity<ClassReportDto> {
        val request = ReportRequest(startDate, endDate, null, null)
        val report = reportService.getClassReport(request)
        return ResponseEntity.ok(report)
    }
    
    @GetMapping("/dashboard")
    fun getDashboardReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime
    ): ResponseEntity<Map<String, Any>> {
        val request = ReportRequest(startDate, endDate, null, null)
        
        val attendanceReport = reportService.getAttendanceReport(request)
        val revenueReport = reportService.getRevenueReport(request)
        val clientReport = reportService.getClientReport(request)
        val classReport = reportService.getClassReport(request)
        
        val dashboard = mapOf(
            "attendance" to attendanceReport,
            "revenue" to revenueReport,
            "clients" to clientReport,
            "classes" to classReport
        )
        
        return ResponseEntity.ok(dashboard)
    }
}


