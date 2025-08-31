package com.nirmaan.student.controller;

import com.nirmaan.student.dto.ApiResponse;
import com.nirmaan.student.dto.UserRegistrationRequest;
import com.nirmaan.student.dto.StudentDto;
import com.nirmaan.student.dto.TrainerDto;
import com.nirmaan.student.dto.CourseDto;
import com.nirmaan.student.dto.FeedbackDto;
import com.nirmaan.student.entity.User;
import com.nirmaan.student.enums.Role;
import com.nirmaan.student.service.UserService;
import com.nirmaan.student.service.StudentService;
import com.nirmaan.student.service.TrainerService;
import com.nirmaan.student.service.CourseService;
import com.nirmaan.student.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final StudentService studentService;
    private final TrainerService trainerService;
    private final CourseService courseService;
    private final FeedbackService feedbackService;

    // User Management
    @PostMapping("/users/register")
    public ResponseEntity<ApiResponse<User>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        User user = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User registered successfully", user));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByRole(@PathVariable Role role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    // Student Management
    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<StudentDto>>> getAllStudents() {
        List<StudentDto> students = studentService.getAllStudents();
        return ResponseEntity.ok(new ApiResponse<>(true, "Students retrieved successfully", students));
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<ApiResponse<StudentDto>> getStudent(@PathVariable Long id) {
        StudentDto student = studentService.getStudentById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Student retrieved successfully", student));
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<ApiResponse<StudentDto>> updateStudent(@PathVariable Long id, 
            @Valid @RequestBody StudentDto studentDto) {
        StudentDto updatedStudent = studentService.updateStudent(id, studentDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Student updated successfully", updatedStudent));
    }

    // Trainer Management
    @GetMapping("/trainers")
    public ResponseEntity<ApiResponse<List<TrainerDto>>> getAllTrainers() {
        List<TrainerDto> trainers = trainerService.getAllTrainers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Trainers retrieved successfully", trainers));
    }

    @GetMapping("/trainers/{id}")
    public ResponseEntity<ApiResponse<TrainerDto>> getTrainer(@PathVariable Long id) {
        TrainerDto trainer = trainerService.getTrainerById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Trainer retrieved successfully", trainer));
    }

    @PutMapping("/trainers/{id}")
    public ResponseEntity<ApiResponse<TrainerDto>> updateTrainer(@PathVariable Long id, 
            @Valid @RequestBody TrainerDto trainerDto) {
        TrainerDto updatedTrainer = trainerService.updateTrainer(id, trainerDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Trainer updated successfully", updatedTrainer));
    }

    // Course Management
    @PostMapping("/courses")
    public ResponseEntity<ApiResponse<CourseDto>> createCourse(@Valid @RequestBody CourseDto courseDto) {
        CourseDto course = courseService.createCourse(courseDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Course created successfully", course));
    }

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<CourseDto>>> getAllCourses() {
        List<CourseDto> courses = courseService.getAllCourses();
        return ResponseEntity.ok(new ApiResponse<>(true, "Courses retrieved successfully", courses));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<CourseDto>> updateCourse(@PathVariable Long id, 
            @Valid @RequestBody CourseDto courseDto) {
        CourseDto updatedCourse = courseService.updateCourse(id, courseDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Course updated successfully", updatedCourse));
    }

    // Feedback Management
    @GetMapping("/feedback")
    public ResponseEntity<ApiResponse<List<FeedbackDto>>> getAllFeedback() {
        List<FeedbackDto> feedback = feedbackService.getAllFeedback();
        return ResponseEntity.ok(new ApiResponse<>(true, "Feedback retrieved successfully", feedback));
    }
}