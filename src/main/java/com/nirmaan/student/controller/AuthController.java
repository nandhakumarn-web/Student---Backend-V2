package com.nirmaan.student.controller;

import com.nirmaan.student.dto.ApiResponse;
import com.nirmaan.student.dto.LoginRequest;
import com.nirmaan.student.dto.LoginResponse;
import com.nirmaan.student.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", loginResponse));
    }
}