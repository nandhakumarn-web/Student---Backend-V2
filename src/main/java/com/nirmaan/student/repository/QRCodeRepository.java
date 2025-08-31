package com.nirmaan.student.repository;

import com.nirmaan.student.entity.QRCode;
import com.nirmaan.student.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface QRCodeRepository extends JpaRepository<QRCode, Long> {
	Optional<QRCode> findByQrCodeId(String qrCodeId);

	List<QRCode> findByBatch(Batch batch);

	List<QRCode> findByValidDate(LocalDate date);

	List<QRCode> findByActiveTrue();
}
