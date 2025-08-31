package com.nirmaan.student.controller;

import com.nirmaan.student.dto.ApiResponse;
import com.nirmaan.student.dto.StudentDto;
import com.nirmaan.student.dto.AttendanceDto;
import com.nirmaan.student.dto.QuizDto;
import com.nirmaan.student.dto.FeedbackDto;
import com.nirmaan.student.entity.StudentQuizAttempt;
import com.nirmaan.student.security.UserPrincipal;
import com.nirmaan.student.service.StudentService;
import com.nirmaan.student.service.AttendanceService;
import com.nirmaan.student.service.QuizService;
import com.nirmaan.student.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'STUDENT')")
public class StudentController {

    private final StudentService studentService;
    private final AttendanceService attendanceService;
    private final QuizService quizService;
    private final FeedbackService feedbackService;

    // Profile Management
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<StudentDto>> getProfile(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        StudentDto student = studentService.getStudentByUserId(userPrincipal.getUser().getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile retrieved successfully", student));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<StudentDto>> updateProfile(@Valid @RequestBody StudentDto studentDto, 
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long studentId = studentService.getStudentByUserId(userPrincipal.getUser().getId()).getId();
        
        StudentDto updatedStudent = studentService.updateStudent(studentId, studentDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully", updatedStudent));
    }

    // Attendance Management
    @PostMapping("/attendance/mark")
    public ResponseEntity<ApiResponse<AttendanceDto>> markAttendance(@RequestParam String qrCodeId, 
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long studentId = studentService.getStudentByUserId(userPrincipal.getUser().getId()).getId();
        
        AttendanceDto attendance = attendanceService.markAttendance(studentId, qrCodeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Attendance marked successfully", attendance));
    }

    @GetMapping("/attendance")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getMyAttendance(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long studentId = studentService.getStudentByUserId(userPrincipal.getUser().getId()).getId();
        
        List<AttendanceDto> attendance = attendanceService.getStudentAttendance(studentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Attendance retrieved successfully", attendance));
    }

    // Quiz Management
    @GetMapping("/quizzes/available")
    public ResponseEntity<ApiResponse<List<QuizDto>>> getAvailableQuizzes() {
        List<QuizDto> quizzes = quizService.getAvailableQuizzes();
        return ResponseEntity.ok(new ApiResponse<>(true, "Available quizzes retrieved successfully", quizzes));
    }

    @PostMapping("/quiz/{quizId}/attempt")
    public ResponseEntity<ApiResponse<StudentQuizAttempt>> submitQuizAttempt(@PathVariable Long quizId, 
            @RequestBody Map<Long, String> answers, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long studentId = studentService.getStudentByUserId(userPrincipal.getUser().getId()).getId();
        
        StudentQuizAttempt attempt = quizService.submitQuizAttempt(studentId, quizId, answers);
        return ResponseEntity.ok(new ApiResponse<>(true, "Quiz submitted successfully", attempt));
    }

    // Feedback Management
    @PostMapping("/feedback")
    public ResponseEntity<ApiResponse<FeedbackDto>> submitFeedback(@Valid @RequestBody FeedbackDto feedbackDto, 
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long studentId = studentService.getStudentByUserId(userPrincipal.getUser().getId()).getId();
        
        FeedbackDto feedback = feedbackService.submitFeedback(feedbackDto, studentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Feedback submitted successfully", feedback));
    }
}