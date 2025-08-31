package com.nirmaan.student.dto;

import java.time.LocalDate;

import com.nirmaan.student.enums.CourseType;
import com.nirmaan.student.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRegistrationRequest {
	@NotBlank
	private String username;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String password;

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;

	@NotNull
	private Role role;

	private String phoneNumber;

	// Student specific fields
	private LocalDate dateOfBirth;
	private String address;
	private String emergencyContact;
	private CourseType enrolledCourse;
	private String qualification;

	// Trainer specific fields
	private String specialization;
	private String qualifications;
	private Integer experienceYears;
	private String certification;
}
