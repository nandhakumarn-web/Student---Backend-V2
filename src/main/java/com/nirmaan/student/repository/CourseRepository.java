package com.nirmaan.student.repository;

import com.nirmaan.student.entity.Course;
import com.nirmaan.student.enums.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
	Optional<Course> findByCourseType(CourseType courseType);

	List<Course> findByActiveTrue();
}