package com.nirmaan.student.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
	private boolean success;
	private String message;
	private T data;

	public ApiResponse(boolean success, String message) {
		this.success = success;
		this.message = message;
	}
}
