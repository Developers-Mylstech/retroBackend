package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.JobApplicantRequest;
import com.mylstech.rentro.dto.response.JobApplicantResponse;

import java.util.List;

public interface JobApplicantService {
    List<JobApplicantResponse> getAllJobApplicants();
    List<JobApplicantResponse> getJobApplicantsByJobPost(Long jobPostId);
    JobApplicantResponse getJobApplicantById(Long id);
    JobApplicantResponse createJobApplicant(JobApplicantRequest request);
    JobApplicantResponse updateJobApplicant(Long id, JobApplicantRequest request);
    void deleteJobApplicant(Long id);
}