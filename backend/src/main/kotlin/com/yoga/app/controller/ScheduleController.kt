package com.yoga.app.controller

import com.yoga.app.dto.*
import com.yoga.app.service.ScheduleService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/schedule")
@CrossOrigin(origins = ["*"])
class ScheduleController(
    private val scheduleService: ScheduleService
) {
    
    // Class Management
    @GetMapping("/classes")
    fun getAllClasses(pageable: Pageable): ResponseEntity<Page<ClassDto>> {
        val classes = scheduleService.getAllClasses(pageable)
        return ResponseEntity.ok(classes)
    }
    
    @GetMapping("/classes/range")
    fun getClassesInDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime
    ): ResponseEntity<List<ClassDto>> {
        val classes = scheduleService.getClassesInDateRange(startDate, endDate)
        return ResponseEntity.ok(classes)
    }
    
    @GetMapping("/classes/{id}")
    fun getClassById(@PathVariable id: Long): ResponseEntity<ClassDto> {
        val clazz = scheduleService.getClassById(id)
        return ResponseEntity.ok(clazz)
    }
    
    @PostMapping("/classes")
    fun createClass(@Valid @RequestBody request: CreateClassRequest): ResponseEntity<ClassDto> {
        val clazz = scheduleService.createClass(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(clazz)
    }
    
    @PutMapping("/classes/{id}")
    fun updateClass(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateClassRequest
    ): ResponseEntity<ClassDto> {
        val clazz = scheduleService.updateClass(id, request)
        return ResponseEntity.ok(clazz)
    }
    
    @DeleteMapping("/classes/{id}")
    fun deleteClass(@PathVariable id: Long): ResponseEntity<Void> {
        scheduleService.deleteClass(id)
        return ResponseEntity.noContent().build()
    }
    
    // Attendance Management
    @GetMapping("/classes/{classId}/attendances")
    fun getAttendancesByClass(@PathVariable classId: Long): ResponseEntity<List<AttendanceDto>> {
        val attendances = scheduleService.getAttendancesByClass(classId)
        return ResponseEntity.ok(attendances)
    }
    
    @GetMapping("/clients/{clientId}/attendances")
    fun getAttendancesByClient(@PathVariable clientId: Long, pageable: Pageable): ResponseEntity<Page<AttendanceDto>> {
        val attendances = scheduleService.getAttendancesByClient(clientId, pageable)
        return ResponseEntity.ok(attendances)
    }
    
    @PostMapping("/attendances")
    fun createAttendance(@Valid @RequestBody request: CreateAttendanceRequest): ResponseEntity<AttendanceDto> {
        val attendance = scheduleService.createAttendance(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(attendance)
    }
    
    @PutMapping("/attendances/{id}")
    fun updateAttendance(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateAttendanceRequest
    ): ResponseEntity<AttendanceDto> {
        val attendance = scheduleService.updateAttendance(id, request)
        return ResponseEntity.ok(attendance)
    }
    
    @DeleteMapping("/attendances/{id}")
    fun deleteAttendance(@PathVariable id: Long): ResponseEntity<Void> {
        scheduleService.deleteAttendance(id)
        return ResponseEntity.noContent().build()
    }
}


