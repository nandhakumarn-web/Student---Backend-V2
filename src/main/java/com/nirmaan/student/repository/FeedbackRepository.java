package com.nirmaan.student.repository;

import com.nirmaan.student.entity.Feedback;
import com.nirmaan.student.entity.Student;
import com.nirmaan.student.entity.Trainer;
import com.nirmaan.student.entity.Course;
import com.nirmaan.student.enums.FeedbackType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
	List<Feedback> findByStudent(Student student);

	List<Feedback> findByTrainer(Trainer trainer);

	List<Feedback> findByCourse(Course course);

	List<Feedback> findByFeedbackType(FeedbackType feedbackType);
}
