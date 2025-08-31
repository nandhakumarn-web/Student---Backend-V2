package com.nirmaan.student.repository;

import com.nirmaan.student.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
	Optional<Trainer> findByTrainerId(String trainerId);

	Optional<Trainer> findByUserId(Long userId);

	List<Trainer> findBySpecialization(String specialization);

	boolean existsByTrainerId(String trainerId);
}
