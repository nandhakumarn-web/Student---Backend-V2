package com.nirmaan.student.enums;

public enum CourseType {
	ITES("Information Technology Enabled Services"),
	JAVA_FULL_STACK("Java Full Stack Development");

	private final String displayName;

	CourseType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

}
