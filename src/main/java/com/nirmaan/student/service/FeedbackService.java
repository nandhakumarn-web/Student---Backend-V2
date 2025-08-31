package com.nirmaan.student.service;

import com.nirmaan.student.dto.FeedbackDto;
import com.nirmaan.student.entity.Feedback;
import com.nirmaan.student.entity.Student;
import com.nirmaan.student.entity.Trainer;
import com.nirmaan.student.entity.Course;
import com.nirmaan.student.enums.FeedbackType;
import com.nirmaan.student.exception.ResourceNotFoundException;
import com.nirmaan.student.exception.ValidationException;
import com.nirmaan.student.repository.FeedbackRepository;
import com.nirmaan.student.repository.StudentRepository;
import com.nirmaan.student.repository.TrainerRepository;
import com.nirmaan.student.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final StudentRepository studentRepository;
    private final TrainerRepository trainerRepository;
    private final CourseRepository courseRepository;

    public FeedbackDto submitFeedback(FeedbackDto feedbackDto, Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Validate rating
        if (feedbackDto.getRating() < 1 || feedbackDto.getRating() > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }

        Feedback feedback = new Feedback();
        feedback.setStudent(student);
        feedback.setFeedbackType(feedbackDto.getFeedbackType());
        feedback.setRating(feedbackDto.getRating());
        feedback.setComments(feedbackDto.getComments());
        feedback.setAnonymous(feedbackDto.isAnonymous());
        feedback.setSubmittedAt(LocalDateTime.now());

        feedback = feedbackRepository.save(feedback);
        return convertToDto(feedback);
    }

    public FeedbackDto submitCourseFeedback(Long studentId, Long courseId, Integer rating, String comments, Boolean anonymous) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }

        Feedback feedback = new Feedback();
        feedback.setStudent(student);
        feedback.setCourse(course);
        feedback.setFeedbackType(FeedbackType.COURSE_FEEDBACK);
        feedback.setRating(rating);
        feedback.setComments(comments);
        feedback.setAnonymous(anonymous);
        feedback.setSubmittedAt(LocalDateTime.now());

        feedback = feedbackRepository.save(feedback);
        return convertToDto(feedback);
    }

    public FeedbackDto submitTrainerFeedback(Long studentId, Long trainerId, Integer rating, String comments, Boolean anonymous) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));

        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }

        Feedback feedback = new Feedback();
        feedback.setStudent(student);
        feedback.setTrainer(trainer);
        feedback.setFeedbackType(FeedbackType.TRAINER_FEEDBACK);
        feedback.setRating(rating);
        feedback.setComments(comments);
        feedback.setAnonymous(anonymous);
        feedback.setSubmittedAt(LocalDateTime.now());

        feedback = feedbackRepository.save(feedback);
        return convertToDto(feedback);
    }

    public FeedbackDto submitSystemFeedback(Long studentId, Integer rating, String comments, Boolean anonymous) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }

        Feedback feedback = new Feedback();
        feedback.setStudent(student);
        feedback.setFeedbackType(FeedbackType.SYSTEM_FEEDBACK);
        feedback.setRating(rating);
        feedback.setComments(comments);
        feedback.setAnonymous(anonymous);
        feedback.setSubmittedAt(LocalDateTime.now());

        feedback = feedbackRepository.save(feedback);
        return convertToDto(feedback);
    }

    public List<FeedbackDto> getAllFeedback() {
        return feedbackRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<FeedbackDto> getFeedbackByTrainer(Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));

        return feedbackRepository.findByTrainer(trainer).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<FeedbackDto> getFeedbackByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        return feedbackRepository.findByStudent(student).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<FeedbackDto> getFeedbackByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        return feedbackRepository.findByCourse(course).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<FeedbackDto> getFeedbackByType(FeedbackType feedbackType) {
        return feedbackRepository.findByFeedbackType(feedbackType).stream()
                .map(this::convertToDto).collect(Collectors.toList());
    }

    public FeedbackDto getFeedbackById(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found"));
        return convertToDto(feedback);
    }

    public FeedbackDto updateFeedback(Long feedbackId, FeedbackDto feedbackDto) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found"));

        // Validate rating
        if (feedbackDto.getRating() < 1 || feedbackDto.getRating() > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }

        feedback.setRating(feedbackDto.getRating());
        feedback.setComments(feedbackDto.getComments());
        feedback.setAnonymous(feedbackDto.isAnonymous());

        feedback = feedbackRepository.save(feedback);
        return convertToDto(feedback);
    }

    public void deleteFeedback(Long feedbackId) {
        if (!feedbackRepository.existsById(feedbackId)) {
            throw new ResourceNotFoundException("Feedback not found");
        }
        feedbackRepository.deleteById(feedbackId);
    }

    // Analytics Methods
    public Map<String, Object> getFeedbackAnalyticsByTrainer(Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));

        List<Feedback> feedbacks = feedbackRepository.findByTrainer(trainer);
        return calculateFeedbackAnalytics(feedbacks, "trainer");
    }

    public Map<String, Object> getFeedbackAnalyticsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        List<Feedback> feedbacks = feedbackRepository.findByCourse(course);
        return calculateFeedbackAnalytics(feedbacks, "course");
    }

    public Map<String, Object> getOverallFeedbackAnalytics() {
        List<Feedback> allFeedbacks = feedbackRepository.findAll();
        return calculateFeedbackAnalytics(allFeedbacks, "overall");
    }

    public Map<String, Object> getFeedbackAnalyticsByType(FeedbackType feedbackType) {
        List<Feedback> feedbacks = feedbackRepository.findByFeedbackType(feedbackType);
        return calculateFeedbackAnalytics(feedbacks, feedbackType.toString().toLowerCase());
    }

    // Summary Methods
    public Map<String, Object> getFeedbackSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        long totalFeedback = feedbackRepository.count();
        long courseFeedback = feedbackRepository.findByFeedbackType(FeedbackType.COURSE_FEEDBACK).size();
        long trainerFeedback = feedbackRepository.findByFeedbackType(FeedbackType.TRAINER_FEEDBACK).size();
        long systemFeedback = feedbackRepository.findByFeedbackType(FeedbackType.SYSTEM_FEEDBACK).size();
        
        summary.put("totalFeedback", totalFeedback);
        summary.put("courseFeedback", courseFeedback);
        summary.put("trainerFeedback", trainerFeedback);
        summary.put("systemFeedback", systemFeedback);
        
        if (totalFeedback > 0) {
            List<Feedback> allFeedbacks = feedbackRepository.findAll();
            double averageRating = allFeedbacks.stream()
                    .mapToInt(Feedback::getRating)
                    .average().orElse(0.0);
            summary.put("averageRating", Math.round(averageRating * 100.0) / 100.0);
        } else {
            summary.put("averageRating", 0.0);
        }
        
        return summary;
    }

    public List<FeedbackDto> getRecentFeedback(int limit) {
        return feedbackRepository.findAll().stream()
                .sorted((f1, f2) -> f2.getSubmittedAt().compareTo(f1.getSubmittedAt()))
                .limit(limit)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<FeedbackDto> getFeedbackByRating(Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }
        
        return feedbackRepository.findAll().stream()
                .filter(feedback -> feedback.getRating().equals(rating))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<FeedbackDto> getAnonymousFeedback() {
        return feedbackRepository.findAll().stream()
                .filter(Feedback::isAnonymous)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<FeedbackDto> getNonAnonymousFeedback() {
        return feedbackRepository.findAll().stream()
                .filter(feedback -> !feedback.isAnonymous())
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Helper Methods
    private Map<String, Object> calculateFeedbackAnalytics(List<Feedback> feedbacks, String type) {
        Map<String, Object> analytics = new HashMap<>();
        
        analytics.put("type", type);
        analytics.put("totalFeedback", feedbacks.size());
        
        if (feedbacks.isEmpty()) {
            analytics.put("averageRating", 0.0);
            analytics.put("ratingDistribution", new HashMap<>());
            return analytics;
        }
        
        // Calculate average rating
        double averageRating = feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average().orElse(0.0);
        analytics.put("averageRating", Math.round(averageRating * 100.0) / 100.0);
        
        // Calculate rating distribution
        Map<Integer, Long> ratingDistribution = feedbacks.stream()
                .collect(Collectors.groupingBy(
                    Feedback::getRating,
                    Collectors.counting()
                ));
        analytics.put("ratingDistribution", ratingDistribution);
        
        // Calculate anonymous vs non-anonymous
        long anonymousCount = feedbacks.stream()
                .filter(Feedback::isAnonymous)
                .count();
        analytics.put("anonymousCount", anonymousCount);
        analytics.put("nonAnonymousCount", feedbacks.size() - anonymousCount);
        
        // Find highest and lowest ratings
        analytics.put("highestRating", feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .max().orElse(0));
        analytics.put("lowestRating", feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .min().orElse(0));
        
        return analytics;
    }

    private FeedbackDto convertToDto(Feedback feedback) {
        FeedbackDto dto = new FeedbackDto();
        dto.setId(feedback.getId());
        if (!feedback.isAnonymous()) {
            dto.setStudentName(feedback.getStudent().getUser().getFirstName() + " "
                    + feedback.getStudent().getUser().getLastName());
        }
        if (feedback.getTrainer() != null) {
            dto.setTrainerName(feedback.getTrainer().getUser().getFirstName() + " "
                    + feedback.getTrainer().getUser().getLastName());
        }
        if (feedback.getCourse() != null) {
            dto.setCourseName(feedback.getCourse().getCourseName());
        }
        dto.setFeedbackType(feedback.getFeedbackType());
        dto.setRating(feedback.getRating());
        dto.setComments(feedback.getComments());
        dto.setAnonymous(feedback.isAnonymous());
        dto.setSubmittedAt(feedback.getSubmittedAt());
        return dto;
    }
}