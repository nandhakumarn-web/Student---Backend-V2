package com.nirmaan.student.controller;

import com.nirmaan.student.dto.ApiResponse;
import com.nirmaan.student.entity.Batch;
import com.nirmaan.student.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
public class BatchController {

    private final BatchService batchService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Batch>>> getAllBatches() {
        List<Batch> batches = batchService.getAllBatches();
        return ResponseEntity.ok(new ApiResponse<>(true, "Batches retrieved successfully", batches));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Batch>> getBatch(@PathVariable Long id) {
        Batch batch = batchService.getBatchById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Batch retrieved successfully", batch));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Batch>> createBatch(@Valid @RequestBody Batch batch) {
        Batch createdBatch = batchService.createBatch(batch);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Batch created successfully", createdBatch));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Batch>> updateBatch(@PathVariable Long id, 
            @Valid @RequestBody Batch batch) {
        Batch updatedBatch = batchService.updateBatch(id, batch);
        return ResponseEntity.ok(new ApiResponse<>(true, "Batch updated successfully", updatedBatch));
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<ApiResponse<List<Batch>>> getBatchesByTrainer(@PathVariable Long trainerId) {
        List<Batch> batches = batchService.getBatchesByTrainer(trainerId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Trainer batches retrieved successfully", batches));
    }
}