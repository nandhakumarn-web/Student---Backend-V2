package com.nirmaan.student.controller;

import com.nirmaan.student.dto.ApiResponse;
import com.nirmaan.student.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminDashboard() {
        Map<String, Object> dashboardData = dashboardService.getAdminDashboardData();
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin dashboard data retrieved", dashboardData));
    }

    @GetMapping("/trainer")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTrainerDashboard() {
        Map<String, Object> dashboardData = dashboardService.getTrainerDashboardData();
        return ResponseEntity.ok(new ApiResponse<>(true, "Trainer dashboard data retrieved", dashboardData));
    }

    @GetMapping("/student")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER', 'STUDENT')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentDashboard() {
        Map<String, Object> dashboardData = dashboardService.getStudentDashboardData();
        return ResponseEntity.ok(new ApiResponse<>(true, "Student dashboard data retrieved", dashboardData));
    }
}