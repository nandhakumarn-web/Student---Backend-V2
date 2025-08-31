package com.nirmaan.student.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "qr_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRCode {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String qrCodeId;
	private String qrCodeData;

	@ManyToOne
	@JoinColumn(name = "batch_id")
	private Batch batch;

	private LocalDate validDate;
	private LocalDateTime generatedAt;
	private LocalDateTime expiresAt;
	private boolean active = true;
}
