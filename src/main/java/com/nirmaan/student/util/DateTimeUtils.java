package com.nirmaan.student.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static String formatDate(LocalDate date) {
		return date != null ? date.format(DATE_FORMATTER) : null;
	}

	public static String formatDateTime(LocalDateTime dateTime) {
		return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
	}

	public static LocalDate parseDate(String dateStr) {
		return dateStr != null ? LocalDate.parse(dateStr, DATE_FORMATTER) : null;
	}

	public static LocalDateTime parseDateTime(String dateTimeStr) {
		return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER) : null;
	}
}