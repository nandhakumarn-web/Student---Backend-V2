package com.nirmaan.student.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

import com.nirmaan.student.enums.FeedbackType;

@Entity
@Table(name = "feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "student_id")
	private Student student;

	@ManyToOne
	@JoinColumn(name = "trainer_id", nullable = true)
	private Trainer trainer;

	@ManyToOne
	@JoinColumn(name = "course_id", nullable = true)
	private Course course;

	@Enumerated(EnumType.STRING)
	private FeedbackType feedbackType;

	private Integer rating; // 1-5 scale

	@Column(columnDefinition = "TEXT")
	private String comments;

	private boolean anonymous = false;
	private LocalDateTime submittedAt;
}