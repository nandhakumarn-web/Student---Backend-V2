package com.nirmaan.student.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.nirmaan.student.enums.CourseType;

@Entity
@Table(name = "quizzes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String description;

	@ManyToOne
	@JoinColumn(name = "trainer_id")
	private Trainer trainer;

	@Enumerated(EnumType.STRING)
	private CourseType courseType;

	@ManyToOne
	@JoinColumn(name = "batch_id")
	private Batch batch;

	private Integer timeLimit; // in minutes
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private boolean active = true;

	@OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Question> questions;
}
