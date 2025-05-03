package com.example.demo.controller;

import com.example.demo.modal.ApiResponse;
import com.example.demo.modal.JobRecruitment;
import com.example.demo.service.JobRecruitmentService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobRecruitmentController {

    @Autowired
    private JobRecruitmentService service;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createJob(@RequestBody JobRecruitment job) {
        JobRecruitment savedJob = service.saveJob(job);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Công việc đã được thêm thành công!");
        response.put("job", savedJob); // Nếu bạn vẫn muốn trả luôn job mới lưu

        return ResponseEntity.ok(response);
    }
    
    // Edit job (Update job)
    @PutMapping("/{id}")
    public ApiResponse editJob(@PathVariable Long id, @RequestBody JobRecruitment job) {
        job.setId(id); // Ensure the job ID is set before updating
        JobRecruitment updatedJob = service.saveJob(job); // Update the job
        if (updatedJob != null) {
            return new ApiResponse(true, "Job updated successfully", updatedJob);
        } else {
            return new ApiResponse(false, "Job not found with ID " + id);
        }
    }
    @GetMapping("/{id}")
    public JobRecruitment getJob(@PathVariable Long id) {
        return service.getJobById(id); // Implement this method in your service layer
    }
    // Delete job
    @DeleteMapping("/{id}")
    public ApiResponse deleteJob(@PathVariable Long id) {
        boolean isDeleted = service.deleteJob(id); // Call the service to delete the job
        if (isDeleted) {
            return new ApiResponse(true, "Job with ID " + id + " has been deleted.");
        } else {
            return new ApiResponse(false, "Job with ID " + id + " not found.");
        }
    }
}
