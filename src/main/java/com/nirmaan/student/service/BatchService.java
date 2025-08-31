package com.nirmaan.student.service;

import com.nirmaan.student.entity.Batch;
import com.nirmaan.student.entity.Trainer;
import com.nirmaan.student.entity.Course;
import com.nirmaan.student.exception.ResourceNotFoundException;
import com.nirmaan.student.repository.BatchRepository;
import com.nirmaan.student.repository.TrainerRepository;
import com.nirmaan.student.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final TrainerRepository trainerRepository;
    private final CourseRepository courseRepository;

    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    public List<Batch> getActiveBatches() {
        return batchRepository.findByActiveTrue();
    }

    public Batch getBatchById(Long id) {
        return batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + id));
    }

    public List<Batch> getBatchesByTrainer(Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
        return batchRepository.findByTrainer(trainer);
    }

    public List<Batch> getBatchesByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return batchRepository.findByCourse(course);
    }

    public Batch createBatch(Batch batch) {
        return batchRepository.save(batch);
    }

    public Batch updateBatch(Long id, Batch batchDetails) {
        Batch batch = getBatchById(id);
        
        batch.setBatchName(batchDetails.getBatchName());
        batch.setCourse(batchDetails.getCourse());
        batch.setTrainer(batchDetails.getTrainer());
        batch.setStartDate(batchDetails.getStartDate());
        batch.setEndDate(batchDetails.getEndDate());
        batch.setMaxStudents(batchDetails.getMaxStudents());
        batch.setSchedule(batchDetails.getSchedule());
        batch.setActive(batchDetails.isActive());
        
        return batchRepository.save(batch);
    }

    public void deleteBatch(Long id) {
        Batch batch = getBatchById(id);
        batch.setActive(false);
        batchRepository.save(batch);
    }
}