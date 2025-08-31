package com.nirmaan.student.controller;

import com.nirmaan.student.dto.ApiResponse;
import com.nirmaan.student.dto.CourseDto;
import com.nirmaan.student.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseDto>>> getAllCourses() {
        List<CourseDto> courses = courseService.getAllCourses();
        return ResponseEntity.ok(new ApiResponse<>(true, "Courses retrieved successfully", courses));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CourseDto>>> getActiveCourses() {
        List<CourseDto> courses = courseService.getActiveCourses();
        return ResponseEntity.ok(new ApiResponse<>(true, "Active courses retrieved successfully", courses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDto>> getCourse(@PathVariable Long id) {
        CourseDto course = courseService.getCourseById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Course retrieved successfully", course));
    }
}