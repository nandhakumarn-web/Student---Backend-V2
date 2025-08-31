package com.nirmaan.student.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.nirmaan.student.enums.AttendanceStatus;

@Entity
@Table(name = "attendance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "student_id")
	private Student student;

	@ManyToOne
	@JoinColumn(name = "batch_id")
	private Batch batch;

	private LocalDate attendanceDate;

	@Enumerated(EnumType.STRING)
	private AttendanceStatus status;

	private LocalDateTime markedAt;
	private String qrCodeId;
}