package com.nirmaan.student.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_quiz_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentQuizAttempt {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "student_id")
	private Student student;

	@ManyToOne
	@JoinColumn(name = "quiz_id")
	private Quiz quiz;

	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Integer totalQuestions;
	private Integer correctAnswers;
	private Integer score;
	private String answers; // JSON string storing answers
	private boolean completed = false;
}