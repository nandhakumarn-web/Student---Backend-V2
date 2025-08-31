package com.nirmaan.student.service;

import com.nirmaan.student.dto.CourseDto;
import com.nirmaan.student.entity.Course;
import com.nirmaan.student.enums.CourseType;
import com.nirmaan.student.exception.ResourceNotFoundException;
import com.nirmaan.student.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

	private final CourseRepository courseRepository;

	public List<CourseDto> getAllCourses() {
		return courseRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public List<CourseDto> getActiveCourses() {
		return courseRepository.findByActiveTrue().stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public CourseDto getCourseById(Long id) {
		Course course = courseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
		return convertToDto(course);
	}

	public CourseDto createCourse(CourseDto courseDto) {
		Course course = new Course();
		course.setCourseType(courseDto.getCourseType());
		course.setCourseName(courseDto.getCourseName());
		course.setDescription(courseDto.getDescription());
		course.setDurationMonths(courseDto.getDurationMonths());
		course.setSyllabus(courseDto.getSyllabus());
		course.setActive(courseDto.isActive());

		course = courseRepository.save(course);
		return convertToDto(course);
	}

	public CourseDto updateCourse(Long id, CourseDto courseDto) {
		Course course = courseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

		course.setCourseName(courseDto.getCourseName());
		course.setDescription(courseDto.getDescription());
		course.setDurationMonths(courseDto.getDurationMonths());
		course.setSyllabus(courseDto.getSyllabus());
		course.setActive(courseDto.isActive());

		course = courseRepository.save(course);
		return convertToDto(course);
	}

	private CourseDto convertToDto(Course course) {
		CourseDto dto = new CourseDto();
		dto.setId(course.getId());
		dto.setCourseType(course.getCourseType());
		dto.setCourseName(course.getCourseName());
		dto.setDescription(course.getDescription());
		dto.setDurationMonths(course.getDurationMonths());
		dto.setSyllabus(course.getSyllabus());
		dto.setActive(course.isActive());
		return dto;
	}
}
