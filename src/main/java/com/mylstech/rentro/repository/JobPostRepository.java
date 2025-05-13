package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    @Query("SELECT j.jobCode FROM JobPost j WHERE j.jobCode LIKE :prefix% ORDER BY j.jobCode DESC")
    String findLatestJobCodeByPrefix(@Param("prefix") String prefix);
    List<JobPost> findByImageImageId(Long imageId);
    @Modifying
    @Query(value = "UPDATE job_posts SET image_id = NULL, image_url = NULL WHERE job_post_id = :jobPostId", nativeQuery = true)
    void clearJobPostImage(@Param("jobPostId") Long jobPostId);
}
