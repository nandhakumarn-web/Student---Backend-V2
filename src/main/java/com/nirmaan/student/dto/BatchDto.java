package com.nirmaan.student.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BatchDto {
	private Long id;
	private String batchName;
	private Long courseId;
	private String courseName;
	private Long trainerId;
	private String trainerName;
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer maxStudents;
	private Integer currentStudents;
	private String schedule;
	private boolean active;
}