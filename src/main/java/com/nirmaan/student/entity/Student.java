package com.nirmaan.student.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

import com.nirmaan.student.enums.CourseType;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String studentId;
    private LocalDate dateOfBirth;
    private String address;
    private String emergencyContact;

    @Enumerated(EnumType.STRING)
    private CourseType enrolledCourse;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private Batch batch;

    private String qualification;
    private LocalDate enrollmentDate;
}
