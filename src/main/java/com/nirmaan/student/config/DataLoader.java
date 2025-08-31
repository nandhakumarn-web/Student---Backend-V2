package com.nirmaan.student.config;

import com.nirmaan.student.entity.User;
import com.nirmaan.student.entity.Course;
import com.nirmaan.student.enums.Role;
import com.nirmaan.student.enums.CourseType;
import com.nirmaan.student.repository.UserRepository;
import com.nirmaan.student.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        loadInitialData();
    }

    private void loadInitialData() {
        // Create default admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@nirmaan.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setRole(Role.ADMIN);
            admin.setPhoneNumber("9999999999");
            admin.setActive(true);
            
            userRepository.save(admin);
            log.info("Default admin user created: username=admin, password=admin123");
        }

        // Create default courses if not exist
        if (courseRepository.findByCourseType(CourseType.ITES).isEmpty()) {
            Course itesCourse = new Course();
            itesCourse.setCourseType(CourseType.ITES);
            itesCourse.setCourseName("Information Technology Enabled Services");
            itesCourse.setDescription("Comprehensive ITES training program covering customer service, data processing, and business process outsourcing");
            itesCourse.setDurationMonths(6);
            itesCourse.setSyllabus("Module 1: Computer Fundamentals\nModule 2: Communication Skills\nModule 3: Customer Service\nModule 4: Data Entry & Processing\nModule 5: Business Process Outsourcing");
            itesCourse.setActive(true);
            
            courseRepository.save(itesCourse);
            log.info("ITES course created");
        }

        if (courseRepository.findByCourseType(CourseType.JAVA_FULL_STACK).isEmpty()) {
            Course javaFullStackCourse = new Course();
            javaFullStackCourse.setCourseType(CourseType.JAVA_FULL_STACK);
            javaFullStackCourse.setCourseName("Java Full Stack Development");
            javaFullStackCourse.setDescription("Complete Java Full Stack development program covering backend and frontend technologies");
            javaFullStackCourse.setDurationMonths(8);
            javaFullStackCourse.setSyllabus("Module 1: Core Java\nModule 2: Advanced Java\nModule 3: Spring Framework\nModule 4: Spring Boot\nModule 5: Database & JPA\nModule 6: Frontend Technologies\nModule 7: Project Development");
            javaFullStackCourse.setActive(true);
            
            courseRepository.save(javaFullStackCourse);
            log.info("Java Full Stack course created");
        }

        log.info("Initial data loading completed");
    }
}