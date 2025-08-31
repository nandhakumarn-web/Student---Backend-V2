package com.nirmaan.student.controller;

import com.nirmaan.student.dto.ApiResponse;
import com.nirmaan.student.dto.FeedbackDto;
import com.nirmaan.student.enums.FeedbackType;
import com.nirmaan.student.security.UserPrincipal;
import com.nirmaan.student.service.FeedbackService;
import com.nirmaan.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final StudentService studentService;

    // ===============================
    // = STUDENT OPERATIONS
    // ===============================

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'STUDENT')")
    public ResponseEntity<ApiResponse<FeedbackDto>> submitFeedback(@Valid @RequestBody FeedbackDto feedbackDto, 
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long studentId = studentService.getStudentByUserId(userPrincipal.getUser().getId()).getId();
        
        FeedbackDto feedback = feedbackService.submitFeedback(feedbackDto, studentId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Feedback submitted successfully", feedback));
    }

    @PostMapping("/course")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'STUDENT')")
    public ResponseEntity<ApiResponse<FeedbackDto>> submitCourseFeedback(
            @RequestParam Long courseId,
            @RequestParam Integer rating,
            @RequestParam String comments,
            @RequestParam(defaultValue = "false") Boolean anonymous,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long studentId = studentService.getStudentByUserId(userPrincipal.getUser().getId()).getId();
        
        FeedbackDto feedback = feedbackService.submitCourseFeedback(studentId, courseId, rating, comments, anonymous);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Course feedback submitted successfully", feedback));
    }

    @PostMapping("/trainer")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'STUDENT')")
    public ResponseEntity<ApiResponse<FeedbackDto>> submitTrainerFeedback(
            @RequestParam Long trainerId,
            @RequestParam Integer rating,
            @RequestParam String comments,
            @RequestParam(defaultValue = "false") Boolean anonymous,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long studentId = studentService.getStudentByUserId(userPrincipal.getUser().getId()).getId();
        
        FeedbackDto feedback = feedbackService.submitTrainerFeedback(studentId, trainerId, rating, comments, anonymous);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Trainer feedback submitted successfully", feedback));
    }

    @PostMapping("/system")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'STUDENT')")
    public ResponseEntity<ApiResponse<FeedbackDto>> submitSystemFeedback(
            @RequestParam Integer rating,
            @RequestParam String comments,
            @RequestParam(defaultValue = "false") Boolean anonymous,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long studentId = studentService.getStudentByUserId(userPrincipal.getUser().getId()).getId();
        
        FeedbackDto feedback = feedbackService.submitSystemFeedback(studentId, rating, comments, anonymous);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "System feedback submitted successfully", feedback));
    }

    @GetMapping("/my-feedback")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<FeedbackDto>>> getMyFeedback(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long studentId = studentService.getStudentByUserId(userPrincipal.getUser().getId()).getId();
        
        List<FeedbackDto> feedback = feedbackService.getFeedbackByStudent(studentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Student feedback retrieved successfully", feedback));
    }

    // ===============================
    // = TRAINER OPERATIONS
    // ===============================

    @GetMapping("/trainer/my-feedback")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<FeedbackDto>>> getTrainerFeedback(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        // This would need TrainerService to get trainerId from userId
        // For now, we'll assume it's handled similarly to student
        
        List<FeedbackDto> feedback = feedbackService.getAllFeedback(); // Temporary - should be trainer-specific
        return ResponseEntity.ok(new ApiResponse<>(true, "Trainer feedback retrieved successfully", feedback));
    }

    @GetMapping("/trainer/{trainerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<FeedbackDto>>> getFeedbackByTrainer(@PathVariable Long trainerId) {
        List<FeedbackDto> feedback = feedbackService.getFeedbackByTrainer(trainerId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Trainer feedback retrieved successfully", feedback));
    }

    // ===============================
    // = ADMIN OPERATIONS
    // ===============================

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<FeedbackDto>>> getAllFeedback() {
        List<FeedbackDto> feedback = feedbackService.getAllFeedback();
        return ResponseEntity.ok(new ApiResponse<>(true, "All feedback retrieved successfully", feedback));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<FeedbackDto>>> getFeedbackByCourse(@PathVariable Long courseId) {
        List<FeedbackDto> feedback = feedbackService.getFeedbackByCourse(courseId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Course feedback retrieved successfully", feedback));
    }

    @GetMapping("/type/{feedbackType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<FeedbackDto>>> getFeedbackByType(@PathVariable FeedbackType feedbackType) {
        List<FeedbackDto> feedback = feedbackService.getFeedbackByType(feedbackType);
        return ResponseEntity.ok(new ApiResponse<>(true, "Feedback by type retrieved successfully", feedback));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<FeedbackDto>>> getFeedbackByStudent(@PathVariable Long studentId) {
        List<FeedbackDto> feedback = feedbackService.getFeedbackByStudent(studentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Student feedback retrieved successfully", feedback));
    }

    @DeleteMapping("/{feedbackId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteFeedback(@PathVariable Long feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Feedback deleted successfully"));
    }

    // ===============================
    // = FEEDBACK ANALYTICS
    // ===============================

    @GetMapping("/analytics/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCourseFeedbackAnalytics(@PathVariable Long courseId) {
        Map<String, Object> analytics = feedbackService.getFeedbackAnalyticsByCourse(courseId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Course feedback analytics retrieved successfully", analytics));
    }

    @GetMapping("/analytics/trainer/{trainerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTrainerFeedbackAnalytics(@PathVariable Long trainerId) {
        Map<String, Object> analytics = feedbackService.getFeedbackAnalyticsByTrainer(trainerId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Trainer feedback analytics retrieved successfully", analytics));
    }

    @GetMapping("/analytics/overall")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverallFeedbackAnalytics() {
        Map<String, Object> analytics = feedbackService.getOverallFeedbackAnalytics();
        return ResponseEntity.ok(new ApiResponse<>(true, "Overall feedback analytics retrieved successfully", analytics));
    }

    @GetMapping("/analytics/type/{feedbackType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFeedbackAnalyticsByType(@PathVariable FeedbackType feedbackType) {
        Map<String, Object> analytics = feedbackService.getFeedbackAnalyticsByType(feedbackType);
        return ResponseEntity.ok(new ApiResponse<>(true, "Feedback analytics by type retrieved successfully", analytics));
    }

    // ===============================
    // = FEEDBACK SUMMARY & REPORTS
    // ===============================

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFeedbackSummary() {
        Map<String, Object> summary = feedbackService.getFeedbackSummary();
        return ResponseEntity.ok(new ApiResponse<>(true, "Feedback summary retrieved successfully", summary));
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<FeedbackDto>>> getRecentFeedback(
            @RequestParam(defaultValue = "10") int limit) {
        List<FeedbackDto> feedback = feedbackService.getRecentFeedback(limit);
        return ResponseEntity.ok(new ApiResponse<>(true, "Recent feedback retrieved successfully", feedback));
    }

    @GetMapping("/rating/{rating}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<FeedbackDto>>> getFeedbackByRating(@PathVariable Integer rating) {
        List<FeedbackDto> feedback = feedbackService.getFeedbackByRating(rating);
        return ResponseEntity.ok(new ApiResponse<>(true, "Feedback by rating retrieved successfully", feedback));
    }

    @GetMapping("/anonymous")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<FeedbackDto>>> getAnonymousFeedback() {
        List<FeedbackDto> feedback = feedbackService.getAnonymousFeedback();
        return ResponseEntity.ok(new ApiResponse<>(true, "Anonymous feedback retrieved successfully", feedback));
    }

    @GetMapping("/non-anonymous")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<FeedbackDto>>> getNonAnonymousFeedback() {
        List<FeedbackDto> feedback = feedbackService.getNonAnonymousFeedback();
        return ResponseEntity.ok(new ApiResponse<>(true, "Non-anonymous feedback retrieved successfully", feedback));
    }

    // ===============================
    // = FEEDBACK CRUD OPERATIONS
    // ===============================

    @GetMapping("/{feedbackId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<FeedbackDto>> getFeedbackById(@PathVariable Long feedbackId) {
        FeedbackDto feedback = feedbackService.getFeedbackById(feedbackId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Feedback retrieved successfully", feedback));
    }

    @PutMapping("/{feedbackId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'STUDENT')")
    public ResponseEntity<ApiResponse<FeedbackDto>> updateFeedback(
            @PathVariable Long feedbackId, 
            @Valid @RequestBody FeedbackDto feedbackDto) {
        FeedbackDto updatedFeedback = feedbackService.updateFeedback(feedbackId, feedbackDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Feedback updated successfully", updatedFeedback));
    }
}