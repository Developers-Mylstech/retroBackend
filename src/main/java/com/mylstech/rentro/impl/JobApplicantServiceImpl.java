package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.JobApplicantRequest;
import com.mylstech.rentro.dto.response.JobApplicantResponse;
import com.mylstech.rentro.model.JobApplicant;
import com.mylstech.rentro.model.JobPost;
import com.mylstech.rentro.repository.JobApplicantRepository;
import com.mylstech.rentro.repository.JobPostRepository;
import com.mylstech.rentro.service.JobApplicantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobApplicantServiceImpl implements JobApplicantService {

    private final JobApplicantRepository jobApplicantRepository;
    private final JobPostRepository jobPostRepository;

    @Override
    public List<JobApplicantResponse> getAllJobApplicants() {
        return jobApplicantRepository.findAll().stream()
                .map(JobApplicantResponse::new)
                .toList();
    }

    @Override
    public List<JobApplicantResponse> getJobApplicantsByJobPost(Long jobPostId) {
        return jobApplicantRepository.findByJobPostJobPostId(jobPostId).stream()
                .map(JobApplicantResponse::new)
                .toList();
    }

    @Override
    public JobApplicantResponse getJobApplicantById(Long id) {
        JobApplicant jobApplicant = jobApplicantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JobApplicant not found with id: " + id));
        return new JobApplicantResponse(jobApplicant);
    }

    @Override
    public JobApplicantResponse createJobApplicant(JobApplicantRequest request) {
        JobPost jobPost = jobPostRepository.findById(request.getJobPostId())
                .orElseThrow(() -> new RuntimeException("JobPost not found with id: " + request.getJobPostId()));
        // Increment total applicants count
        jobPost.setTotalApplicants(jobPost.getTotalApplicants() + 1);
        jobPostRepository.save(jobPost);

        JobApplicant jobApplicant = request.requestToJobApplicant(jobPost);
        return new JobApplicantResponse(jobApplicantRepository.save(jobApplicant));
    }

    @Override
    public JobApplicantResponse updateJobApplicant(Long id, JobApplicantRequest request) {
        JobApplicant jobApplicant = jobApplicantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JobApplicant not found with id: " + id));

        if (request.getName() != null) {
            jobApplicant.setName(request.getName());
        }
        if (request.getEmail() != null) {
            jobApplicant.setEmail(request.getEmail());
        }
        if (request.getResume() != null) {
            jobApplicant.setResume(request.getResume());
        }
        if (request.getJobPostId() != null) {
            JobPost jobPost = jobPostRepository.findById(request.getJobPostId())
                    .orElseThrow(() -> new RuntimeException("JobPost not found with id: " + request.getJobPostId()));
            jobApplicant.setJobPost(jobPost);
        }

        return new JobApplicantResponse(jobApplicantRepository.save(jobApplicant));
    }

    @Override
    public void deleteJobApplicant(Long id) {
        JobApplicant jobApplicant = jobApplicantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JobApplicant not found with id: " + id));
        jobApplicantRepository.delete(jobApplicant);
    }
}