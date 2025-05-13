package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.JobPostRequest;
import com.mylstech.rentro.dto.response.JobPostResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JobPostService {
    List<JobPostResponse> getAllJobPosts();
    JobPostResponse getJobPostById(Long id);
    JobPostResponse createJobPost(JobPostRequest request);
    JobPostResponse updateJobPost(Long id, JobPostRequest request);
    void deleteJobPost(Long id);
    void updateJobPostCount();
    
    /**
     * Set the image for a job post
     * @param jobPostId the job post ID
     * @param imageId the image ID
     * @return the updated job post response
     */
    @Transactional
    JobPostResponse setJobPostImage(Long jobPostId, Long imageId);
    
    /**
     * Remove the image from a job post
     * @param jobPostId the job post ID
     * @return the updated job post response
     */
    @Transactional
    JobPostResponse removeJobPostImage(Long jobPostId);
}
