package com.nirmaan.student.repository;

import com.nirmaan.student.entity.StudentQuizAttempt;
import com.nirmaan.student.entity.Student;
import com.nirmaan.student.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentQuizAttemptRepository extends JpaRepository<StudentQuizAttempt, Long> {
	List<StudentQuizAttempt> findByStudent(Student student);

	List<StudentQuizAttempt> findByQuiz(Quiz quiz);

	Optional<StudentQuizAttempt> findByStudentAndQuiz(Student student, Quiz quiz);
}
