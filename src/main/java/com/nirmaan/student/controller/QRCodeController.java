package com.nirmaan.student.controller;

import com.nirmaan.student.dto.ApiResponse;
import com.nirmaan.student.entity.QRCode;
import com.nirmaan.student.service.QRCodeService;
import com.nirmaan.student.util.QRCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/qrcode")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
public class QRCodeController {

    private final QRCodeService qrCodeService;
    private final QRCodeGenerator qrCodeGenerator;

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> getQRCodeForBatch(
            @PathVariable Long batchId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        if (date == null) {
            date = LocalDate.now();
        }
        
        QRCode qrCode = qrCodeService.getQRCodeForBatch(batchId, date);
        if (qrCode == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "No QR code found for the specified batch and date"));
        }

        try {
            String qrCodeImage = qrCodeGenerator.generateQRCodeImage(qrCode.getQrCodeData());
            Map<String, String> qrData = new HashMap<>();
            qrData.put("qrCodeId", qrCode.getQrCodeId());
            qrData.put("qrCodeImage", qrCodeImage);
            qrData.put("validDate", qrCode.getValidDate().toString());
            qrData.put("expiresAt", qrCode.getExpiresAt().toString());
            
            return ResponseEntity.ok(new ApiResponse<>(true, "QR code retrieved successfully", qrData));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Error generating QR code image: " + e.getMessage()));
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<String>> generateDailyQRCodes() {
        qrCodeService.generateDailyQRCodes();
        return ResponseEntity.ok(new ApiResponse<>(true, "Daily QR codes generated successfully"));
    }
}