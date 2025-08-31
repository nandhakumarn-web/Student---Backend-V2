package com.nirmaan.student.dto;

import lombok.Data;

import java.time.LocalDate;

import com.nirmaan.student.enums.CourseType;

@Data
public class StudentDto {
	private Long id;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String studentId;
	private LocalDate dateOfBirth;
	private String address;
	private String emergencyContact;
	private CourseType enrolledCourse;
	private String qualification;
	private LocalDate enrollmentDate;
	private String batchName;
}