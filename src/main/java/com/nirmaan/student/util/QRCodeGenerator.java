package com.nirmaan.student.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

@Component
public class QRCodeGenerator {

	public String generateQRCodeData(Long batchId, LocalDate date) {
		return "BATCH:" + batchId + ":DATE:" + date.toString() + ":TIME:" + System.currentTimeMillis();
	}

	public String generateQRCodeImage(String data) throws WriterException, IOException {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();

		Map<EncodeHintType, Object> hints = new HashMap<>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

		BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 300, 300, hints);

		BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < 300; x++) {
			for (int y = 0; y < 300; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
			}
		}

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(image, "PNG", outputStream);
		byte[] imageBytes = outputStream.toByteArray();

		return Base64.getEncoder().encodeToString(imageBytes);
	}
}
