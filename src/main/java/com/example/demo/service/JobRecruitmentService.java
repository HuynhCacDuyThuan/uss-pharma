package com.example.demo.service;

import com.example.demo.modal.JobRecruitment;
import com.example.demo.repository.JobRecruitmentRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobRecruitmentService {

    @Autowired
    private JobRecruitmentRepository repository;

    public JobRecruitment saveJob(JobRecruitment job) {
        return repository.save(job);
    }
    public boolean deleteJob(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
    public List<JobRecruitment> getAllJobs() {
        return repository.findAll(); // Fetch all jobs from the repository
    }
    public JobRecruitment getJobById(Long id) {
        Optional<JobRecruitment> job = repository.findById(id); // Retrieve the job by ID from the repository
        return job.orElse(null); // Return job if present, or null if not found
    }
}
