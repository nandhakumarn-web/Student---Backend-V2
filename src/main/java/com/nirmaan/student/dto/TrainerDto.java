package com.nirmaan.student.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TrainerDto {
	private Long id;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String trainerId;
	private String specialization;
	private String qualifications;
	private Integer experienceYears;
	private LocalDate joiningDate;
	private String certification;
}
