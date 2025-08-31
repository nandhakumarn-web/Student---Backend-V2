package com.nirmaan.student.service;

import com.nirmaan.student.entity.QRCode;
import com.nirmaan.student.entity.Batch;
import com.nirmaan.student.repository.QRCodeRepository;
import com.nirmaan.student.repository.BatchRepository;
import com.nirmaan.student.util.QRCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QRCodeService {

	private final QRCodeRepository qrCodeRepository;
	private final BatchRepository batchRepository;
	private final QRCodeGenerator qrCodeGenerator;

	@Scheduled(cron = "0 0 8 * * *") // Generate daily at 8 AM
	public void generateDailyQRCodes() {
		List<Batch> activeBatches = batchRepository.findByActiveTrue();
		LocalDate today = LocalDate.now();

		for (Batch batch : activeBatches) {
			String qrCodeId = UUID.randomUUID().toString();
			String qrCodeData = qrCodeGenerator.generateQRCodeData(batch.getId(), today);

			QRCode qrCode = new QRCode();
			qrCode.setQrCodeId(qrCodeId);
			qrCode.setQrCodeData(qrCodeData);
			qrCode.setBatch(batch);
			qrCode.setValidDate(today);
			qrCode.setGeneratedAt(LocalDateTime.now());
			qrCode.setExpiresAt(LocalDateTime.now().plusHours(10)); // Expires after 10 hours
			qrCode.setActive(true);

			qrCodeRepository.save(qrCode);
		}
	}

	public QRCode getQRCodeForBatch(Long batchId, LocalDate date) {
		Batch batch = batchRepository.findById(batchId).orElse(null);
		if (batch == null)
			return null;

		return qrCodeRepository.findByBatch(batch).stream()
				.filter(qr -> qr.getValidDate().equals(date) && qr.isActive()).findFirst().orElse(null);
	}
}
