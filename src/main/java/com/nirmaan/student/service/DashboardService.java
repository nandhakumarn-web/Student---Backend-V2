package com.nirmaan.student.service;

import com.nirmaan.student.repository.*;
import com.nirmaan.student.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TrainerRepository trainerRepository;
    private final CourseRepository courseRepository;
    private final BatchRepository batchRepository;
    private final QuizRepository quizRepository;
    private final AttendanceRepository attendanceRepository;
    private final FeedbackRepository feedbackRepository;

    public Map<String, Object> getAdminDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        // User statistics
        data.put("totalUsers", userRepository.count());
        data.put("totalStudents", userRepository.findByRole(Role.STUDENT).size());
        data.put("totalTrainers", userRepository.findByRole(Role.TRAINER).size());
        data.put("activeUsers", userRepository.findByActiveTrue().size());
        
        // Course and batch statistics
        data.put("totalCourses", courseRepository.count());
        data.put("activeCourses", courseRepository.findByActiveTrue().size());
        data.put("totalBatches", batchRepository.count());
        data.put("activeBatches", batchRepository.findByActiveTrue().size());
        
        // Quiz statistics
        data.put("totalQuizzes", quizRepository.count());
        data.put("activeQuizzes", quizRepository.findByActiveTrue().size());
        
        // Today's attendance
        data.put("todayAttendance", attendanceRepository.findByAttendanceDate(LocalDate.now()).size());
        
        // Total feedback
        data.put("totalFeedback", feedbackRepository.count());
        
        return data;
    }

    public Map<String, Object> getTrainerDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        // Basic statistics
        data.put("totalBatches", batchRepository.findByActiveTrue().size());
        data.put("totalQuizzes", quizRepository.findByActiveTrue().size());
        data.put("todayAttendance", attendanceRepository.findByAttendanceDate(LocalDate.now()).size());
        
        return data;
    }

    public Map<String, Object> getStudentDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        // Basic statistics
        data.put("availableQuizzes", quizRepository.findByActiveTrue().size());
        data.put("totalCourses", courseRepository.findByActiveTrue().size());
        
        return data;
    }
}