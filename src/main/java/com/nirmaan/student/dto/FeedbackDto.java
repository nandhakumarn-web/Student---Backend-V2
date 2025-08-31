package com.nirmaan.student.dto;

import lombok.Data;

import java.time.LocalDateTime;

import com.nirmaan.student.enums.FeedbackType;

@Data
public class FeedbackDto {
	private Long id;
	private String studentName;
	private String trainerName;
	private String courseName;
	private FeedbackType feedbackType;
	private Integer rating;
	private String comments;
	private boolean anonymous;
	private LocalDateTime submittedAt;
}
