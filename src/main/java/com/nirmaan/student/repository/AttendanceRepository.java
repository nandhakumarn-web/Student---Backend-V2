package com.nirmaan.student.repository;

import com.nirmaan.student.entity.Attendance;
import com.nirmaan.student.entity.Student;
import com.nirmaan.student.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	List<Attendance> findByStudent(Student student);

	List<Attendance> findByBatch(Batch batch);

	List<Attendance> findByAttendanceDate(LocalDate date);

	Optional<Attendance> findByStudentAndAttendanceDate(Student student, LocalDate date);

	@Query("SELECT a FROM Attendance a WHERE a.student = :student AND a.attendanceDate BETWEEN :startDate AND :endDate")
	List<Attendance> findByStudentAndDateRange(@Param("student") Student student,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
