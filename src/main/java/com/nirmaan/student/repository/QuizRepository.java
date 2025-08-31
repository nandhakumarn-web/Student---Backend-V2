package com.nirmaan.student.repository;

import com.nirmaan.student.entity.Quiz;
import com.nirmaan.student.entity.Trainer;
import com.nirmaan.student.entity.Batch;
import com.nirmaan.student.enums.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
	List<Quiz> findByTrainer(Trainer trainer);

	List<Quiz> findByBatch(Batch batch);

	List<Quiz> findByCourseType(CourseType courseType);

	List<Quiz> findByActiveTrue();
}
