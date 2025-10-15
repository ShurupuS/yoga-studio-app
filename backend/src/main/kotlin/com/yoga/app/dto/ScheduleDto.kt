package com.yoga.app.dto

import com.yoga.app.entity.Attendance
import com.yoga.app.entity.AttendanceStatus
import com.yoga.app.entity.Class
import com.yoga.app.entity.ClassStatus
import java.time.LocalDateTime

data class ClassDto(
    val id: Long,
    val name: String,
    val description: String?,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val maxCapacity: Int,
    val currentCapacity: Int,
    val status: ClassStatus,
    val notes: String?,
    val createdAt: LocalDateTime,
    val attendanceCount: Int = 0
) {
    companion object {
        fun from(clazz: Class): ClassDto {
            return ClassDto(
                id = clazz.id,
                name = clazz.name,
                description = clazz.description,
                startTime = clazz.startTime,
                endTime = clazz.endTime,
                maxCapacity = clazz.maxCapacity,
                currentCapacity = clazz.currentCapacity,
                status = clazz.status,
                notes = clazz.notes,
                createdAt = clazz.createdAt,
                attendanceCount = clazz.attendances.size
            )
        }
    }
}

data class AttendanceDto(
    val id: Long,
    val clientId: Long,
    val clientName: String,
    val classId: Long,
    val className: String,
    val classStartTime: LocalDateTime,
    val status: AttendanceStatus,
    val notes: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(attendance: Attendance): AttendanceDto {
            return AttendanceDto(
                id = attendance.id,
                clientId = attendance.client.id,
                clientName = attendance.client.name,
                classId = attendance.class.id,
                className = attendance.class.name,
                classStartTime = attendance.class.startTime,
                status = attendance.status,
                notes = attendance.notes,
                createdAt = attendance.createdAt
            )
        }
    }
}

data class CreateClassRequest(
    val name: String,
    val description: String? = null,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val maxCapacity: Int,
    val notes: String? = null
)

data class UpdateClassRequest(
    val name: String? = null,
    val description: String? = null,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null,
    val maxCapacity: Int? = null,
    val status: ClassStatus? = null,
    val notes: String? = null
)

data class CreateAttendanceRequest(
    val clientId: Long,
    val classId: Long,
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val notes: String? = null
)

data class UpdateAttendanceRequest(
    val status: AttendanceStatus? = null,
    val notes: String? = null
)


