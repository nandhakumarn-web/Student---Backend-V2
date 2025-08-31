package com.nirmaan.student.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "quiz_id")
	private Quiz quiz;

	@Column(columnDefinition = "TEXT")
	private String questionText;

	private String optionA;
	private String optionB;
	private String optionC;
	private String optionD;
	private String correctAnswer; // A, B, C, or D
	private Integer marks = 1;
}
