package com.nirmaan.student.service;

import com.nirmaan.student.dto.StudentDto;
import com.nirmaan.student.entity.Student;
import com.nirmaan.student.entity.User;
import com.nirmaan.student.exception.ResourceNotFoundException;
import com.nirmaan.student.repository.StudentRepository;
import com.nirmaan.student.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

	private final StudentRepository studentRepository;
	private final UserRepository userRepository;

	public List<StudentDto> getAllStudents() {
		return studentRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public StudentDto getStudentById(Long id) {
		Student student = studentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
		return convertToDto(student);
	}

	public StudentDto getStudentByUserId(Long userId) {
		Student student = studentRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Student not found for user id: " + userId));
		return convertToDto(student);
	}

	public StudentDto updateStudent(Long id, StudentDto studentDto) {
		Student student = studentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

		User user = student.getUser();
		user.setFirstName(studentDto.getFirstName());
		user.setLastName(studentDto.getLastName());
		user.setPhoneNumber(studentDto.getPhoneNumber());
		userRepository.save(user);

		student.setDateOfBirth(studentDto.getDateOfBirth());
		student.setAddress(studentDto.getAddress());
		student.setEmergencyContact(studentDto.getEmergencyContact());
		student.setQualification(studentDto.getQualification());

		student = studentRepository.save(student);
		return convertToDto(student);
	}

	private StudentDto convertToDto(Student student) {
		StudentDto dto = new StudentDto();
		dto.setId(student.getId());
		dto.setUsername(student.getUser().getUsername());
		dto.setEmail(student.getUser().getEmail());
		dto.setFirstName(student.getUser().getFirstName());
		dto.setLastName(student.getUser().getLastName());
		dto.setPhoneNumber(student.getUser().getPhoneNumber());
		dto.setStudentId(student.getStudentId());
		dto.setDateOfBirth(student.getDateOfBirth());
		dto.setAddress(student.getAddress());
		dto.setEmergencyContact(student.getEmergencyContact());
		dto.setEnrolledCourse(student.getEnrolledCourse());
		dto.setQualification(student.getQualification());
		dto.setEnrollmentDate(student.getEnrollmentDate());
		if (student.getBatch() != null) {
			dto.setBatchName(student.getBatch().getBatchName());
		}
		return dto;
	}
}