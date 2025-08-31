package com.nirmaan.student.service;

import com.nirmaan.student.dto.TrainerDto;
import com.nirmaan.student.entity.Trainer;
import com.nirmaan.student.entity.User;
import com.nirmaan.student.exception.ResourceNotFoundException;
import com.nirmaan.student.repository.TrainerRepository;
import com.nirmaan.student.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainerService {

	private final TrainerRepository trainerRepository;
	private final UserRepository userRepository;

	public List<TrainerDto> getAllTrainers() {
		return trainerRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public TrainerDto getTrainerById(Long id) {
		Trainer trainer = trainerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + id));
		return convertToDto(trainer);
	}

	public TrainerDto getTrainerByUserId(Long userId) {
		Trainer trainer = trainerRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Trainer not found for user id: " + userId));
		return convertToDto(trainer);
	}

	public TrainerDto updateTrainer(Long id, TrainerDto trainerDto) {
		Trainer trainer = trainerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + id));

		User user = trainer.getUser();
		user.setFirstName(trainerDto.getFirstName());
		user.setLastName(trainerDto.getLastName());
		user.setPhoneNumber(trainerDto.getPhoneNumber());
		userRepository.save(user);

		trainer.setSpecialization(trainerDto.getSpecialization());
		trainer.setQualifications(trainerDto.getQualifications());
		trainer.setExperienceYears(trainerDto.getExperienceYears());
		trainer.setCertification(trainerDto.getCertification());

		trainer = trainerRepository.save(trainer);
		return convertToDto(trainer);
	}

	private TrainerDto convertToDto(Trainer trainer) {
		TrainerDto dto = new TrainerDto();
		dto.setId(trainer.getId());
		dto.setUsername(trainer.getUser().getUsername());
		dto.setEmail(trainer.getUser().getEmail());
		dto.setFirstName(trainer.getUser().getFirstName());
		dto.setLastName(trainer.getUser().getLastName());
		dto.setPhoneNumber(trainer.getUser().getPhoneNumber());
		dto.setTrainerId(trainer.getTrainerId());
		dto.setSpecialization(trainer.getSpecialization());
		dto.setQualifications(trainer.getQualifications());
		dto.setExperienceYears(trainer.getExperienceYears());
		dto.setJoiningDate(trainer.getJoiningDate());
		dto.setCertification(trainer.getCertification());
		return dto;
	}
}