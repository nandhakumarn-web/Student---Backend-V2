package com.nirmaan.student.entity;

import com.nirmaan.student.enums.CourseType;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(unique = true)
	private CourseType courseType;

	private String courseName;
	private String description;
	private Integer durationMonths;
	private String syllabus;
	private boolean active = true;
}