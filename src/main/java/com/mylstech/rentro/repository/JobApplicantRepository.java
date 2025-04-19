package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.JobApplicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicantRepository extends JpaRepository<JobApplicant, Long> {
    List<JobApplicant> findByJobPostJobPostId(Long jobPostId);
}