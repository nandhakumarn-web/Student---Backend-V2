package com.nirmaan.student.service;

import com.nirmaan.student.dto.QuizDto;
import com.nirmaan.student.dto.QuestionDto;
import com.nirmaan.student.entity.Quiz;
import com.nirmaan.student.entity.Question;
import com.nirmaan.student.entity.Trainer;
import com.nirmaan.student.entity.Batch;
import com.nirmaan.student.entity.StudentQuizAttempt;
import com.nirmaan.student.entity.Student;
import com.nirmaan.student.enums.CourseType;
import com.nirmaan.student.exception.ResourceNotFoundException;
import com.nirmaan.student.exception.ValidationException;
import com.nirmaan.student.repository.QuizRepository;
import com.nirmaan.student.repository.QuestionRepository;
import com.nirmaan.student.repository.TrainerRepository;
import com.nirmaan.student.repository.BatchRepository;
import com.nirmaan.student.repository.StudentQuizAttemptRepository;
import com.nirmaan.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final TrainerRepository trainerRepository;
    private final BatchRepository batchRepository;
    private final StudentQuizAttemptRepository studentQuizAttemptRepository;
    private final StudentRepository studentRepository;

    // Basic CRUD Operations
    public List<QuizDto> getAllQuizzes() {
        return quizRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public QuizDto getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
        return convertToDto(quiz);
    }

    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new ResourceNotFoundException("Quiz not found");
        }
        quizRepository.deleteById(id);
    }

    @Transactional
    public QuizDto createQuiz(QuizDto quizDto, Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));

        Batch batch = null;
        if (quizDto.getBatchName() != null && !quizDto.getBatchName().isEmpty()) {
            try {
                Long batchId = Long.parseLong(quizDto.getBatchName());
                batch = batchRepository.findById(batchId).orElse(null);
            } catch (NumberFormatException e) {
                // Handle case where batchName is not a number
            }
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDto.getTitle());
        quiz.setDescription(quizDto.getDescription());
        quiz.setTrainer(trainer);
        quiz.setCourseType(quizDto.getCourseType());
        quiz.setBatch(batch);
        quiz.setTimeLimit(quizDto.getTimeLimit());
        quiz.setStartTime(quizDto.getStartTime());
        quiz.setEndTime(quizDto.getEndTime());
        quiz.setActive(quizDto.isActive());

        quiz = quizRepository.save(quiz);

        // Create questions
        if (quizDto.getQuestions() != null) {
            for (QuestionDto questionDto : quizDto.getQuestions()) {
                Question question = new Question();
                question.setQuiz(quiz);
                question.setQuestionText(questionDto.getQuestionText());
                question.setOptionA(questionDto.getOptionA());
                question.setOptionB(questionDto.getOptionB());
                question.setOptionC(questionDto.getOptionC());
                question.setOptionD(questionDto.getOptionD());
                question.setCorrectAnswer(questionDto.getCorrectAnswer());
                question.setMarks(questionDto.getMarks());
                questionRepository.save(question);
            }
        }

        return convertToDto(quiz);
    }

    @Transactional
    public QuizDto updateQuiz(Long id, QuizDto quizDto, Long trainerId) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        // Check if trainer owns this quiz or is admin
        if (!quiz.getTrainer().getId().equals(trainerId)) {
            throw new ValidationException("You can only update your own quizzes");
        }

        quiz.setTitle(quizDto.getTitle());
        quiz.setDescription(quizDto.getDescription());
        quiz.setTimeLimit(quizDto.getTimeLimit());
        quiz.setStartTime(quizDto.getStartTime());
        quiz.setEndTime(quizDto.getEndTime());
        quiz.setActive(quizDto.isActive());

        quiz = quizRepository.save(quiz);
        return convertToDto(quiz);
    }

    public List<QuizDto> getQuizzesByTrainer(Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));

        return quizRepository.findByTrainer(trainer).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<QuizDto> getQuizzesByCourseType(CourseType courseType) {
        return quizRepository.findByCourseType(courseType).stream()
                .map(this::convertToDto).collect(Collectors.toList());
    }

    public void activateQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
        quiz.setActive(true);
        quizRepository.save(quiz);
    }

    public void deactivateQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
        quiz.setActive(false);
        quizRepository.save(quiz);
    }

    public List<QuizDto> getAvailableQuizzes() {
        return quizRepository.findByActiveTrue().stream()
                .filter(quiz -> quiz.getStartTime().isBefore(LocalDateTime.now())
                        && quiz.getEndTime().isAfter(LocalDateTime.now()))
                .map(this::convertToDto).collect(Collectors.toList());
    }

    public List<QuizDto> getAvailableQuizzesForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        return quizRepository.findByActiveTrue().stream()
                .filter(quiz -> quiz.getStartTime().isBefore(LocalDateTime.now())
                        && quiz.getEndTime().isAfter(LocalDateTime.now()))
                .filter(quiz -> quiz.getCourseType() == null || quiz.getCourseType().equals(student.getEnrolledCourse()))
                .filter(quiz -> quiz.getBatch() == null || quiz.getBatch().equals(student.getBatch()))
                .map(this::convertToDto).collect(Collectors.toList());
    }

    public StudentQuizAttempt submitQuizAttempt(Long studentId, Long quizId, Map<Long, String> answers) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        if (studentQuizAttemptRepository.findByStudentAndQuiz(student, quiz).isPresent()) {
            throw new ValidationException("Quiz already attempted");
        }

        if (!quiz.isActive() || LocalDateTime.now().isAfter(quiz.getEndTime())) {
            throw new ValidationException("Quiz is not available for submission");
        }

        List<Question> questions = questionRepository.findByQuiz(quiz);
        int correctAnswers = 0;
        int totalQuestions = questions.size();

        for (Question question : questions) {
            String studentAnswer = answers.get(question.getId());
            if (studentAnswer != null && studentAnswer.equals(question.getCorrectAnswer())) {
                correctAnswers++;
            }
        }

        int score = totalQuestions > 0 ? (correctAnswers * 100) / totalQuestions : 0;

        StudentQuizAttempt attempt = new StudentQuizAttempt();
        attempt.setStudent(student);
        attempt.setQuiz(quiz);
        attempt.setStartTime(LocalDateTime.now().minusMinutes(quiz.getTimeLimit()));
        attempt.setEndTime(LocalDateTime.now());
        attempt.setTotalQuestions(totalQuestions);
        attempt.setCorrectAnswers(correctAnswers);
        attempt.setScore(score);
        attempt.setAnswers(answers.toString()); // Convert to JSON in real implementation
        attempt.setCompleted(true);

        return studentQuizAttemptRepository.save(attempt);
    }

    public List<StudentQuizAttempt> getStudentQuizAttempts(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return studentQuizAttemptRepository.findByStudent(student);
    }

    public List<StudentQuizAttempt> getQuizAttempts(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
        return studentQuizAttemptRepository.findByQuiz(quiz);
    }

    // Question Management
    public QuestionDto addQuestionToQuiz(Long quizId, QuestionDto questionDto) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        Question question = new Question();
        question.setQuiz(quiz);
        question.setQuestionText(questionDto.getQuestionText());
        question.setOptionA(questionDto.getOptionA());
        question.setOptionB(questionDto.getOptionB());
        question.setOptionC(questionDto.getOptionC());
        question.setOptionD(questionDto.getOptionD());
        question.setCorrectAnswer(questionDto.getCorrectAnswer());
        question.setMarks(questionDto.getMarks());

        question = questionRepository.save(question);
        return convertQuestionToDto(question);
    }

    public QuestionDto updateQuestion(Long questionId, QuestionDto questionDto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        question.setQuestionText(questionDto.getQuestionText());
        question.setOptionA(questionDto.getOptionA());
        question.setOptionB(questionDto.getOptionB());
        question.setOptionC(questionDto.getOptionC());
        question.setOptionD(questionDto.getOptionD());
        question.setCorrectAnswer(questionDto.getCorrectAnswer());
        question.setMarks(questionDto.getMarks());

        question = questionRepository.save(question);
        return convertQuestionToDto(question);
    }

    public void deleteQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException("Question not found");
        }
        questionRepository.deleteById(questionId);
    }

    public List<QuestionDto> getQuizQuestions(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        List<Question> questions = questionRepository.findByQuiz(quiz);
        return questions.stream().map(this::convertQuestionToDto).collect(Collectors.toList());
    }

    // Analytics
    public Map<String, Object> getQuizAnalytics(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        List<StudentQuizAttempt> attempts = studentQuizAttemptRepository.findByQuiz(quiz);
        Map<String, Object> analytics = new HashMap<>();

        analytics.put("totalAttempts", attempts.size());
        analytics.put("averageScore", attempts.stream().mapToInt(StudentQuizAttempt::getScore).average().orElse(0.0));
        analytics.put("highestScore", attempts.stream().mapToInt(StudentQuizAttempt::getScore).max().orElse(0));
        analytics.put("lowestScore", attempts.stream().mapToInt(StudentQuizAttempt::getScore).min().orElse(0));
        analytics.put("completionRate", attempts.stream().filter(StudentQuizAttempt::isCompleted).count() * 100.0 / attempts.size());

        return analytics;
    }

    public List<Map<String, Object>> getQuizResults(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        List<StudentQuizAttempt> attempts = studentQuizAttemptRepository.findByQuiz(quiz);
        List<Map<String, Object>> results = new ArrayList<>();

        for (StudentQuizAttempt attempt : attempts) {
            Map<String, Object> result = new HashMap<>();
            result.put("studentName", attempt.getStudent().getUser().getFirstName() + " " + 
                      attempt.getStudent().getUser().getLastName());
            result.put("score", attempt.getScore());
            result.put("correctAnswers", attempt.getCorrectAnswers());
            result.put("totalQuestions", attempt.getTotalQuestions());
            result.put("completedAt", attempt.getEndTime());
            results.add(result);
        }

        return results;
    }

    // Helper methods
    private QuizDto convertToDto(Quiz quiz) {
        QuizDto dto = new QuizDto();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        if (quiz.getTrainer() != null) {
            dto.setTrainerName(
                    quiz.getTrainer().getUser().getFirstName() + " " + quiz.getTrainer().getUser().getLastName());
        }
        dto.setCourseType(quiz.getCourseType());
        if (quiz.getBatch() != null) {
            dto.setBatchName(quiz.getBatch().getBatchName());
        }
        dto.setTimeLimit(quiz.getTimeLimit());
        dto.setStartTime(quiz.getStartTime());
        dto.setEndTime(quiz.getEndTime());
        dto.setActive(quiz.isActive());

        List<Question> questions = questionRepository.findByQuiz(quiz);
        dto.setQuestions(questions.stream().map(this::convertQuestionToDto).collect(Collectors.toList()));

        return dto;
    }

    private QuestionDto convertQuestionToDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setQuestionText(question.getQuestionText());
        dto.setOptionA(question.getOptionA());
        dto.setOptionB(question.getOptionB());
        dto.setOptionC(question.getOptionC());
        dto.setOptionD(question.getOptionD());
        // Don't expose correct answer to students in normal scenarios
        dto.setMarks(question.getMarks());
        return dto;
    }
}