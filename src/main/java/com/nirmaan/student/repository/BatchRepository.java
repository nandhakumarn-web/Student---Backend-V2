package com.nirmaan.student.repository;

import com.nirmaan.student.entity.Batch;
import com.nirmaan.student.entity.Trainer;
import com.nirmaan.student.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
	List<Batch> findByTrainer(Trainer trainer);

	List<Batch> findByCourse(Course course);

	List<Batch> findByActiveTrue();
}
