package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    @Query("SELECT j.jobCode FROM JobPost j WHERE j.jobCode LIKE :prefix% ORDER BY j.jobCode DESC")
    String findLatestJobCodeByPrefix(@Param("prefix") String prefix);
}