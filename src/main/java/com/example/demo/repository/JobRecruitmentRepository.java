package com.example.demo.repository;

import com.example.demo.modal.JobRecruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRecruitmentRepository extends JpaRepository<JobRecruitment, Long> {
}
