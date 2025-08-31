package com.nirmaan.student.controller;

import com.nirmaan.student.dto.ApiResponse;
import com.nirmaan.student.dto.QuizDto;
import com.nirmaan.student.dto.QuestionDto;
import com.nirmaan.student.entity.StudentQuizAttempt;
import com.nirmaan.student.enums.CourseType;
import com.nirmaan.student.security.UserPrincipal;
import com.nirmaan.student.service.QuizService;
import com.nirmaan.student.service.TrainerService;
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
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final TrainerService trainerService;
    private final StudentService studentService;

    // ===============================
    // = ADMIN OPERATIONS
    // ===============================

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<QuizDto>>> getAllQuizzes() {
        List<QuizDto> quizzes = quizService.getAllQuizzes();
        return ResponseEntity.ok(new ApiResponse<>(true, "All quizzes retrieved successfully", quizzes));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<QuizDto>> getQuizById(@PathVariable Long id) {
        QuizDto quiz = quizService.getQuizById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Quiz retrieved successfully", quiz));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Quiz deleted successfully"));
    }

    // ===============================
    // = TRAINER OPERATIONS
    // ===============================

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<QuizDto>> createQuiz(@Valid @RequestBody QuizDto quizDto, 
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long trainerId = trainerService.getTrainerByUserId(userPrincipal.getUser().getId()).getId();
        
        QuizDto createdQuiz = quizService.createQuiz(quizDto, trainerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Quiz created successfully", createdQuiz));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<QuizDto>> updateQuiz(@PathVariable Long id, 
            @Valid @RequestBody QuizDto quizDto, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long trainerId = trainerService.getTrainerByUserId(userPrincipal.getUser().getId()).getId();
        
        QuizDto updatedQuiz = quizService.updateQuiz(id, quizDto, trainerId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Quiz updated successfully", updatedQuiz));
    }

    @GetMapping("/trainer/my-quizzes")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<QuizDto>>> getMyQuizzes(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long trainerId = trainerService.getTrainerByUserId(userPrincipal.getUser().getId()).getId();
        
        List<QuizDto> quizzes = quizService.getQuizzesByTrainer(trainerId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Trainer quizzes retrieved successfully", quizzes));
    }

    @GetMapping("/course/{courseType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<QuizDto>>> getQuizzesByCourse(@PathVariable CourseType courseType) {
        List<QuizDto> quizzes = quizService.getQuizzesByCourseType(courseType);
        return ResponseEntity.ok(new ApiResponse<>(true, "Course quizzes retrieved successfully", quizzes));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<String>> activateQuiz(@PathVariable Long id) {
        quizService.activateQuiz(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Quiz activated successfully"));
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<String>> deactivateQuiz(@PathVariable Long id) {
        quizService.deactivateQuiz(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Quiz deactivated successfully"));
    }

    // ===============================
    // = STUDENT OPERATIONS
    // ===============================

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<QuizDto>>> getAvailableQuizzes() {
        List<QuizDto> quizzes = quizService.getAvailableQuizzes();
        return ResponseEntity.ok(new ApiResponse<>(true, "Available quizzes retrieved successfully", quizzes));
    }

    @GetMapping("/student/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<QuizDto>>> getAvailableQuizzesForStudent(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long studentId = studentService.getStudentByUserId(userPrincipal.getUser().getId()).getId();
        
        List<QuizDto> quizzes = quizService.getAvailableQuizzesForStudent(studentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Available quizzes for student retrieved successfully", quizzes));
    }

    @PostMapping("/{id}/attempt")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'STUDENT')")
    public ResponseEntity<ApiResponse<StudentQuizAttempt>> submitQuizAttempt(@PathVariable Long id, 
            @RequestBody Map<Long, String> answers, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long studentId = studentService.getStudentByUserId(userPrincipal.getUser().getId()).getId();
        
        StudentQuizAttempt attempt = quizService.submitQuizAttempt(studentId, id, answers);
        return ResponseEntity.ok(new ApiResponse<>(true, "Quiz submitted successfully", attempt));
    }

    @GetMapping("/student/attempts")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<StudentQuizAttempt>>> getMyQuizAttempts(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long studentId = studentService.getStudentByUserId(userPrincipal.getUser().getId()).getId();
        
        List<StudentQuizAttempt> attempts = quizService.getStudentQuizAttempts(studentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Student quiz attempts retrieved successfully", attempts));
    }

    @GetMapping("/{quizId}/attempts")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<StudentQuizAttempt>>> getQuizAttempts(@PathVariable Long quizId) {
        List<StudentQuizAttempt> attempts = quizService.getQuizAttempts(quizId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Quiz attempts retrieved successfully", attempts));
    }

    // ===============================
    // = QUESTION MANAGEMENT
    // ===============================

    @PostMapping("/{quizId}/questions")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<QuestionDto>> addQuestionToQuiz(@PathVariable Long quizId, 
            @Valid @RequestBody QuestionDto questionDto) {
        QuestionDto createdQuestion = quizService.addQuestionToQuiz(quizId, questionDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Question added successfully", createdQuestion));
    }

    @PutMapping("/questions/{questionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<QuestionDto>> updateQuestion(@PathVariable Long questionId, 
            @Valid @RequestBody QuestionDto questionDto) {
        QuestionDto updatedQuestion = quizService.updateQuestion(questionId, questionDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Question updated successfully", updatedQuestion));
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<String>> deleteQuestion(@PathVariable Long questionId) {
        quizService.deleteQuestion(questionId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Question deleted successfully"));
    }

    @GetMapping("/{quizId}/questions")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<QuestionDto>>> getQuizQuestions(@PathVariable Long quizId) {
        List<QuestionDto> questions = quizService.getQuizQuestions(quizId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Quiz questions retrieved successfully", questions));
    }

    // ===============================
    // = QUIZ ANALYTICS
    // ===============================

    @GetMapping("/{quizId}/analytics")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getQuizAnalytics(@PathVariable Long quizId) {
        Map<String, Object> analytics = quizService.getQuizAnalytics(quizId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Quiz analytics retrieved successfully", analytics));
    }

    @GetMapping("/{quizId}/results")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getQuizResults(@PathVariable Long quizId) {
        List<Map<String, Object>> results = quizService.getQuizResults(quizId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Quiz results retrieved successfully", results));
    }
}