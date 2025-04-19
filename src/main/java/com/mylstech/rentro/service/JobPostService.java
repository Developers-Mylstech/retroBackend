package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.JobPostRequest;
import com.mylstech.rentro.dto.response.JobPostResponse;

import java.util.List;

public interface JobPostService {
    List<JobPostResponse> getAllJobPosts();
    JobPostResponse getJobPostById(Long id);
    JobPostResponse createJobPost(JobPostRequest request);
    JobPostResponse updateJobPost(Long id, JobPostRequest request);
    void deleteJobPost(Long id);
}