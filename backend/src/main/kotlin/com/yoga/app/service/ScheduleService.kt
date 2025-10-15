package com.yoga.app.service

import com.yoga.app.dto.*
import com.yoga.app.entity.Attendance
import com.yoga.app.entity.AttendanceStatus
import com.yoga.app.entity.Class
import com.yoga.app.entity.ClassStatus
import com.yoga.app.repository.AttendanceRepository
import com.yoga.app.repository.ClassRepository
import com.yoga.app.repository.ClientRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ScheduleService(
    private val classRepository: ClassRepository,
    private val attendanceRepository: AttendanceRepository,
    private val clientRepository: ClientRepository
) {
    
    // Class Management
    fun getAllClasses(pageable: Pageable): Page<ClassDto> {
        return classRepository.findAll(pageable)
            .map { ClassDto.from(it) }
    }
    
    fun getClassesInDateRange(startDate: LocalDateTime, endDate: LocalDateTime): List<ClassDto> {
        return classRepository.findClassesInDateRange(startDate, endDate)
            .map { ClassDto.from(it) }
    }
    
    fun getClassById(id: Long): ClassDto {
        val clazz = classRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Class not found with id: $id") }
        return ClassDto.from(clazz)
    }
    
    fun createClass(request: CreateClassRequest): ClassDto {
        val clazz = Class(
            name = request.name,
            description = request.description,
            startTime = request.startTime,
            endTime = request.endTime,
            maxCapacity = request.maxCapacity,
            notes = request.notes
        )
        
        val savedClass = classRepository.save(clazz)
        return ClassDto.from(savedClass)
    }
    
    fun updateClass(id: Long, request: UpdateClassRequest): ClassDto {
        val clazz = classRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Class not found with id: $id") }
        
        val updatedClass = clazz.copy(
            name = request.name ?: clazz.name,
            description = request.description ?: clazz.description,
            startTime = request.startTime ?: clazz.startTime,
            endTime = request.endTime ?: clazz.endTime,
            maxCapacity = request.maxCapacity ?: clazz.maxCapacity,
            status = request.status ?: clazz.status,
            notes = request.notes ?: clazz.notes,
            updatedAt = LocalDateTime.now()
        )
        
        val savedClass = classRepository.save(updatedClass)
        return ClassDto.from(savedClass)
    }
    
    fun deleteClass(id: Long) {
        val clazz = classRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Class not found with id: $id") }
        
        val cancelledClass = clazz.copy(
            status = ClassStatus.CANCELLED,
            updatedAt = LocalDateTime.now()
        )
        
        classRepository.save(cancelledClass)
    }
    
    // Attendance Management
    fun getAttendancesByClass(classId: Long): List<AttendanceDto> {
        return attendanceRepository.findByClassId(classId)
            .map { AttendanceDto.from(it) }
    }
    
    fun getAttendancesByClient(clientId: Long, pageable: Pageable): Page<AttendanceDto> {
        return attendanceRepository.findByClientId(clientId, pageable)
            .map { AttendanceDto.from(it) }
    }
    
    fun createAttendance(request: CreateAttendanceRequest): AttendanceDto {
        val client = clientRepository.findById(request.clientId)
            .orElseThrow { IllegalArgumentException("Client not found with id: ${request.clientId}") }
        
        val clazz = classRepository.findById(request.classId)
            .orElseThrow { IllegalArgumentException("Class not found with id: ${request.classId}") }
        
        // Check if attendance already exists
        val existingAttendance = attendanceRepository.findByClientIdAndClassId(request.clientId, request.classId)
        if (existingAttendance != null) {
            throw IllegalArgumentException("Attendance already exists for this client and class")
        }
        
        val attendance = Attendance(
            client = client,
            class = clazz,
            status = request.status,
            notes = request.notes
        )
        
        val savedAttendance = attendanceRepository.save(attendance)
        
        // Update class capacity
        updateClassCapacity(request.classId)
        
        return AttendanceDto.from(savedAttendance)
    }
    
    fun updateAttendance(id: Long, request: UpdateAttendanceRequest): AttendanceDto {
        val attendance = attendanceRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Attendance not found with id: $id") }
        
        val updatedAttendance = attendance.copy(
            status = request.status ?: attendance.status,
            notes = request.notes ?: attendance.notes
        )
        
        val savedAttendance = attendanceRepository.save(updatedAttendance)
        
        // Update class capacity
        updateClassCapacity(attendance.class.id)
        
        return AttendanceDto.from(savedAttendance)
    }
    
    fun deleteAttendance(id: Long) {
        val attendance = attendanceRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Attendance not found with id: $id") }
        
        attendanceRepository.delete(attendance)
        
        // Update class capacity
        updateClassCapacity(attendance.class.id)
    }
    
    private fun updateClassCapacity(classId: Long) {
        val clazz = classRepository.findById(classId).orElse(null) ?: return
        val presentCount = attendanceRepository.findByClassId(classId)
            .count { it.status == AttendanceStatus.PRESENT }
        
        val updatedClass = clazz.copy(
            currentCapacity = presentCount,
            updatedAt = LocalDateTime.now()
        )
        
        classRepository.save(updatedClass)
    }
}


