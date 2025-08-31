package com.nirmaan.student.controller;

import com.nirmaan.student.dto.ApiResponse;
import com.nirmaan.student.dto.AttendanceDto;
import com.nirmaan.student.enums.AttendanceStatus;
import com.nirmaan.student.security.UserPrincipal;
import com.nirmaan.student.service.AttendanceService;
import com.nirmaan.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final StudentService studentService;

    // ===============================
    // = STUDENT OPERATIONS
    // ===============================

    @PostMapping("/mark")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'STUDENT')")
    public ResponseEntity<ApiResponse<AttendanceDto>> markAttendance(@RequestParam String qrCodeId, 
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long studentId = studentService.getStudentByUserId(userPrincipal.getUser().getId()).getId();
        
        AttendanceDto attendance = attendanceService.markAttendance(studentId, qrCodeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Attendance marked successfully", attendance));
    }

    @PostMapping("/mark-manual")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<AttendanceDto>> markManualAttendance(
            @RequestParam Long studentId,
            @RequestParam AttendanceStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        if (date == null) {
            date = LocalDate.now();
        }
        
        AttendanceDto attendance = attendanceService.markManualAttendance(studentId, status, date);
        return ResponseEntity.ok(new ApiResponse<>(true, "Manual attendance marked successfully", attendance));
    }

    @GetMapping("/my-attendance")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getMyAttendance(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long studentId = studentService.getStudentByUserId(userPrincipal.getUser().getId()).getId();
        
        List<AttendanceDto> attendance = attendanceService.getStudentAttendance(studentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Student attendance retrieved successfully", attendance));
    }

    @GetMapping("/my-attendance/range")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getMyAttendanceByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long studentId = studentService.getStudentByUserId(userPrincipal.getUser().getId()).getId();
        
        List<AttendanceDto> attendance = attendanceService.getStudentAttendanceByDateRange(studentId, startDate, endDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Student attendance for date range retrieved successfully", attendance));
    }

    // ===============================
    // = TRAINER/ADMIN OPERATIONS
    // ===============================

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceDto> attendance = attendanceService.getAttendanceByDate(date);
        return ResponseEntity.ok(new ApiResponse<>(true, "Attendance for date retrieved successfully", attendance));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getStudentAttendance(@PathVariable Long studentId) {
        List<AttendanceDto> attendance = attendanceService.getStudentAttendance(studentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Student attendance retrieved successfully", attendance));
    }

    @GetMapping("/student/{studentId}/range")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getStudentAttendanceByDateRange(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<AttendanceDto> attendance = attendanceService.getStudentAttendanceByDateRange(studentId, startDate, endDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Student attendance for date range retrieved successfully", attendance));
    }

    @GetMapping("/batch/{batchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getBatchAttendance(@PathVariable Long batchId) {
        List<AttendanceDto> attendance = attendanceService.getBatchAttendance(batchId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Batch attendance retrieved successfully", attendance));
    }

    @GetMapping("/batch/{batchId}/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getBatchAttendanceByDate(
            @PathVariable Long batchId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<AttendanceDto> attendance = attendanceService.getBatchAttendanceByDate(batchId, date);
        return ResponseEntity.ok(new ApiResponse<>(true, "Batch attendance for date retrieved successfully", attendance));
    }

    @PutMapping("/{attendanceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<AttendanceDto>> updateAttendance(
            @PathVariable Long attendanceId,
            @RequestParam AttendanceStatus status) {
        
        AttendanceDto updatedAttendance = attendanceService.updateAttendanceStatus(attendanceId, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Attendance updated successfully", updatedAttendance));
    }

    @DeleteMapping("/{attendanceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteAttendance(@PathVariable Long attendanceId) {
        attendanceService.deleteAttendance(attendanceId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Attendance record deleted successfully"));
    }

    // ===============================
    // = ATTENDANCE ANALYTICS
    // ===============================

    @GetMapping("/analytics/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentAttendanceAnalytics(@PathVariable Long studentId) {
        Map<String, Object> analytics = attendanceService.getStudentAttendanceAnalytics(studentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Student attendance analytics retrieved successfully", analytics));
    }

    @GetMapping("/analytics/batch/{batchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBatchAttendanceAnalytics(@PathVariable Long batchId) {
        Map<String, Object> analytics = attendanceService.getBatchAttendanceAnalytics(batchId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Batch attendance analytics retrieved successfully", analytics));
    }

    @GetMapping("/analytics/overall")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverallAttendanceAnalytics() {
        Map<String, Object> analytics = attendanceService.getOverallAttendanceAnalytics();
        return ResponseEntity.ok(new ApiResponse<>(true, "Overall attendance analytics retrieved successfully", analytics));
    }

    @GetMapping("/report/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateStudentAttendanceReport(
            @PathVariable Long studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        Map<String, Object> report = attendanceService.generateStudentAttendanceReport(studentId, startDate, endDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Student attendance report generated successfully", report));
    }

    @GetMapping("/report/batch/{batchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateBatchAttendanceReport(
            @PathVariable Long batchId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        Map<String, Object> report = attendanceService.generateBatchAttendanceReport(batchId, startDate, endDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Batch attendance report generated successfully", report));
    }

    // ===============================
    // = ATTENDANCE SUMMARY
    // ===============================

    @GetMapping("/summary/today")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTodayAttendanceSummary() {
        Map<String, Object> summary = attendanceService.getTodayAttendanceSummary();
        return ResponseEntity.ok(new ApiResponse<>(true, "Today's attendance summary retrieved successfully", summary));
    }

    @GetMapping("/summary/weekly")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getWeeklyAttendanceSummary() {
        Map<String, Object> summary = attendanceService.getWeeklyAttendanceSummary();
        return ResponseEntity.ok(new ApiResponse<>(true, "Weekly attendance summary retrieved successfully", summary));
    }

    @GetMapping("/summary/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMonthlyAttendanceSummary() {
        Map<String, Object> summary = attendanceService.getMonthlyAttendanceSummary();
        return ResponseEntity.ok(new ApiResponse<>(true, "Monthly attendance summary retrieved successfully", summary));
    }

    // ===============================
    // = BULK OPERATIONS
    // ===============================

    @PostMapping("/bulk-mark")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> bulkMarkAttendance(
            @RequestBody Map<Long, AttendanceStatus> studentAttendanceMap,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        if (date == null) {
            date = LocalDate.now();
        }
        
        List<AttendanceDto> attendanceList = attendanceService.bulkMarkAttendance(studentAttendanceMap, date);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bulk attendance marked successfully", attendanceList));
    }

    @PostMapping("/batch/{batchId}/mark-all-present")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> markAllBatchStudentsPresent(
            @PathVariable Long batchId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        if (date == null) {
            date = LocalDate.now();
        }
        
        List<AttendanceDto> attendanceList = attendanceService.markAllBatchStudentsPresent(batchId, date);
        return ResponseEntity.ok(new ApiResponse<>(true, "All batch students marked present successfully", attendanceList));
    }
}