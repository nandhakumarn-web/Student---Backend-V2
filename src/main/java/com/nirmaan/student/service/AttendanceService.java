package com.nirmaan.student.service;

import com.nirmaan.student.dto.AttendanceDto;
import com.nirmaan.student.entity.Attendance;
import com.nirmaan.student.entity.Student;
import com.nirmaan.student.entity.Batch;
import com.nirmaan.student.entity.QRCode;
import com.nirmaan.student.enums.AttendanceStatus;
import com.nirmaan.student.exception.ResourceNotFoundException;
import com.nirmaan.student.exception.ValidationException;
import com.nirmaan.student.repository.AttendanceRepository;
import com.nirmaan.student.repository.StudentRepository;
import com.nirmaan.student.repository.BatchRepository;
import com.nirmaan.student.repository.QRCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final BatchRepository batchRepository;
    private final QRCodeRepository qrCodeRepository;

    public AttendanceDto markAttendance(Long studentId, String qrCodeId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        QRCode qrCode = qrCodeRepository.findByQrCodeId(qrCodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid QR Code"));

        if (!qrCode.isActive() || qrCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ValidationException("QR Code has expired");
        }

        if (attendanceRepository.findByStudentAndAttendanceDate(student, LocalDate.now()).isPresent()) {
            throw new ValidationException("Attendance already marked for today");
        }

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setBatch(student.getBatch());
        attendance.setAttendanceDate(LocalDate.now());
        attendance.setStatus(AttendanceStatus.PRESENT);
        attendance.setMarkedAt(LocalDateTime.now());
        attendance.setQrCodeId(qrCodeId);

        attendance = attendanceRepository.save(attendance);
        return convertToDto(attendance);
    }

    public AttendanceDto markManualAttendance(Long studentId, AttendanceStatus status, LocalDate date) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (attendanceRepository.findByStudentAndAttendanceDate(student, date).isPresent()) {
            throw new ValidationException("Attendance already marked for this date");
        }

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setBatch(student.getBatch());
        attendance.setAttendanceDate(date);
        attendance.setStatus(status);
        attendance.setMarkedAt(LocalDateTime.now());

        attendance = attendanceRepository.save(attendance);
        return convertToDto(attendance);
    }

    public List<AttendanceDto> getStudentAttendance(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        return attendanceRepository.findByStudent(student).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AttendanceDto> getStudentAttendanceByDateRange(Long studentId, LocalDate startDate, LocalDate endDate) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        return attendanceRepository.findByStudentAndDateRange(student, startDate, endDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AttendanceDto> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByAttendanceDate(date).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AttendanceDto> getBatchAttendance(Long batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        return attendanceRepository.findByBatch(batch).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AttendanceDto> getBatchAttendanceByDate(Long batchId, LocalDate date) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        return attendanceRepository.findByBatch(batch).stream()
                .filter(a -> a.getAttendanceDate().equals(date))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public AttendanceDto updateAttendanceStatus(Long attendanceId, AttendanceStatus status) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found"));

        attendance.setStatus(status);
        attendance = attendanceRepository.save(attendance);
        return convertToDto(attendance);
    }

    public void deleteAttendance(Long attendanceId) {
        if (!attendanceRepository.existsById(attendanceId)) {
            throw new ResourceNotFoundException("Attendance record not found");
        }
        attendanceRepository.deleteById(attendanceId);
    }

    public Map<String, Object> getStudentAttendanceAnalytics(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        List<Attendance> attendances = attendanceRepository.findByStudent(student);
        Map<String, Object> analytics = new HashMap<>();
        
        long totalDays = attendances.size();
        long presentDays = attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long absentDays = attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        long lateDays = attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
        
        analytics.put("totalDays", totalDays);
        analytics.put("presentDays", presentDays);
        analytics.put("absentDays", absentDays);
        analytics.put("lateDays", lateDays);
        analytics.put("attendancePercentage", totalDays > 0 ? (presentDays * 100.0 / totalDays) : 0);
        
        return analytics;
    }

    public Map<String, Object> getBatchAttendanceAnalytics(Long batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        List<Attendance> attendances = attendanceRepository.findByBatch(batch);
        Map<String, Object> analytics = new HashMap<>();
        
        analytics.put("totalRecords", attendances.size());
        analytics.put("presentCount", attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count());
        analytics.put("absentCount", attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count());
        analytics.put("lateCount", attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count());
        
        return analytics;
    }

    public Map<String, Object> getOverallAttendanceAnalytics() {
        List<Attendance> allAttendances = attendanceRepository.findAll();
        Map<String, Object> analytics = new HashMap<>();
        
        analytics.put("totalRecords", allAttendances.size());
        analytics.put("presentCount", allAttendances.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count());
        analytics.put("absentCount", allAttendances.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count());
        analytics.put("lateCount", allAttendances.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count());
        
        return analytics;
    }

    public Map<String, Object> generateStudentAttendanceReport(Long studentId, LocalDate startDate, LocalDate endDate) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        List<Attendance> attendances = attendanceRepository.findByStudentAndDateRange(student, startDate, endDate);
        Map<String, Object> report = new HashMap<>();
        
        report.put("studentId", studentId);
        report.put("studentName", student.getUser().getFirstName() + " " + student.getUser().getLastName());
        report.put("reportPeriod", startDate + " to " + endDate);
        report.put("attendanceRecords", attendances.stream().map(this::convertToDto).collect(Collectors.toList()));
        report.put("analytics", getStudentAttendanceAnalytics(studentId));
        
        return report;
    }

    public Map<String, Object> generateBatchAttendanceReport(Long batchId, LocalDate startDate, LocalDate endDate) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        List<Attendance> attendances = attendanceRepository.findByBatch(batch).stream()
                .filter(a -> !a.getAttendanceDate().isBefore(startDate) && !a.getAttendanceDate().isAfter(endDate))
                .collect(Collectors.toList());
        
        Map<String, Object> report = new HashMap<>();
        report.put("batchId", batchId);
        report.put("batchName", batch.getBatchName());
        report.put("reportPeriod", startDate + " to " + endDate);
        report.put("attendanceRecords", attendances.stream().map(this::convertToDto).collect(Collectors.toList()));
        
        return report;
    }

    public Map<String, Object> getTodayAttendanceSummary() {
        List<Attendance> todayAttendances = attendanceRepository.findByAttendanceDate(LocalDate.now());
        Map<String, Object> summary = new HashMap<>();
        
        summary.put("date", LocalDate.now());
        summary.put("totalMarked", todayAttendances.size());
        summary.put("present", todayAttendances.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count());
        summary.put("absent", todayAttendances.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count());
        summary.put("late", todayAttendances.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count());
        
        return summary;
    }

    public Map<String, Object> getWeeklyAttendanceSummary() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("period", "Last 7 days");
        summary.put("startDate", startDate);
        summary.put("endDate", endDate);
        
        return summary;
    }

    public Map<String, Object> getMonthlyAttendanceSummary() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("period", "Last 30 days");
        summary.put("startDate", startDate);
        summary.put("endDate", endDate);
        
        return summary;
    }

    public List<AttendanceDto> bulkMarkAttendance(Map<Long, AttendanceStatus> studentAttendanceMap, LocalDate date) {
        List<AttendanceDto> results = new ArrayList<>();
        
        for (Map.Entry<Long, AttendanceStatus> entry : studentAttendanceMap.entrySet()) {
            try {
                AttendanceDto attendance = markManualAttendance(entry.getKey(), entry.getValue(), date);
                results.add(attendance);
            } catch (ValidationException e) {
                // Skip if already marked
            }
        }
        
        return results;
    }

    public List<AttendanceDto> markAllBatchStudentsPresent(Long batchId, LocalDate date) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        List<Student> students = studentRepository.findByBatch(batch);
        List<AttendanceDto> results = new ArrayList<>();

        for (Student student : students) {
            try {
                AttendanceDto attendance = markManualAttendance(student.getId(), AttendanceStatus.PRESENT, date);
                results.add(attendance);
            } catch (ValidationException e) {
                // Skip if already marked
            }
        }

        return results;
    }

    private AttendanceDto convertToDto(Attendance attendance) {
        AttendanceDto dto = new AttendanceDto();
        dto.setId(attendance.getId());
        dto.setStudentId(attendance.getStudent().getId());
        dto.setStudentName(attendance.getStudent().getUser().getFirstName() + " "
                + attendance.getStudent().getUser().getLastName());
        if (attendance.getBatch() != null) {
            dto.setBatchName(attendance.getBatch().getBatchName());
        }
        dto.setAttendanceDate(attendance.getAttendanceDate());
        dto.setStatus(attendance.getStatus());
        dto.setMarkedAt(attendance.getMarkedAt());
        return dto;
    }
}