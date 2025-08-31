package com.nirmaan.student.service;

import com.nirmaan.student.dto.UserRegistrationRequest;
import com.nirmaan.student.entity.User;
import com.nirmaan.student.entity.Student;
import com.nirmaan.student.entity.Trainer;
import com.nirmaan.student.enums.Role;
import com.nirmaan.student.exception.ValidationException;
import com.nirmaan.student.repository.UserRepository;
import com.nirmaan.student.repository.StudentRepository;
import com.nirmaan.student.repository.TrainerRepository;
import com.nirmaan.student.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final StudentRepository studentRepository;
	private final TrainerRepository trainerRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
		return new UserPrincipal(user);
	}

	@Transactional
	public User registerUser(UserRegistrationRequest request) {
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new ValidationException("Username already exists");
		}
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new ValidationException("Email already exists");
		}

		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setRole(request.getRole());
		user.setPhoneNumber(request.getPhoneNumber());

		user = userRepository.save(user);

		// Create role-specific entities
		if (request.getRole() == Role.STUDENT) {
			createStudentProfile(user, request);
		} else if (request.getRole() == Role.TRAINER) {
			createTrainerProfile(user, request);
		}

		return user;
	}

	private void createStudentProfile(User user, UserRegistrationRequest request) {
		Student student = new Student();
		student.setUser(user);
		student.setStudentId("STD" + System.currentTimeMillis());
		student.setDateOfBirth(request.getDateOfBirth());
		student.setAddress(request.getAddress());
		student.setEmergencyContact(request.getEmergencyContact());
		student.setEnrolledCourse(request.getEnrolledCourse());
		student.setQualification(request.getQualification());
		student.setEnrollmentDate(LocalDate.now());
		studentRepository.save(student);
	}

	private void createTrainerProfile(User user, UserRegistrationRequest request) {
		Trainer trainer = new Trainer();
		trainer.setUser(user);
		trainer.setTrainerId("TRN" + System.currentTimeMillis());
		trainer.setSpecialization(request.getSpecialization());
		trainer.setQualifications(request.getQualifications());
		trainer.setExperienceYears(request.getExperienceYears());
		trainer.setCertification(request.getCertification());
		trainer.setJoiningDate(LocalDate.now());
		trainerRepository.save(trainer);
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public List<User> getUsersByRole(Role role) {
		return userRepository.findByRole(role);
	}
}