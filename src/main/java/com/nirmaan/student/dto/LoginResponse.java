package com.nirmaan.student.dto;

import lombok.Data;

import com.nirmaan.student.enums.Role;

import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponse {
	private String token;
	private String username;
	private String email;
	private Role role;
	private String firstName;
	private String lastName;
}