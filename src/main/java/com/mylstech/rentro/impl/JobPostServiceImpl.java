package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.JobPostRequest;
import com.mylstech.rentro.dto.response.JobPostResponse;
import com.mylstech.rentro.model.JobPost;
import com.mylstech.rentro.repository.JobPostRepository;
import com.mylstech.rentro.service.JobPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobPostServiceImpl implements JobPostService {

    private final JobPostRepository jobPostRepository;

    @Override
    public List<JobPostResponse> getAllJobPosts() {
        return jobPostRepository.findAll().stream()
                .map(JobPostResponse::new)
                .toList();
    }

    @Override
    public JobPostResponse getJobPostById(Long id) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JobPost not found with id: " + id));
        return new JobPostResponse(jobPost);
    }

    @Override
    public JobPostResponse createJobPost(JobPostRequest request) {
        JobPost jobPost = request.requestToJobPost();
        return new JobPostResponse(jobPostRepository.save(jobPost));
    }

    @Override
    public JobPostResponse updateJobPost(Long id, JobPostRequest request) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JobPost not found with id: " + id));

        if (request.getJobTitle() != null) {
            jobPost.setJobTitle(request.getJobTitle());
        }
        if (request.getJobDescription() != null) {
            jobPost.setJobDescription(request.getJobDescription());
        }
        if (request.getRequirements() != null) {
            jobPost.setRequirements(request.getRequirements());
        }
        if (request.getIsActive() != null) {
            jobPost.setIsActive(request.getIsActive());
        }
        if (request.getImage() != null) {
            jobPost.setImage(request.getImage());
        }

        return new JobPostResponse(jobPostRepository.save(jobPost));
    }

    @Override
    public void deleteJobPost(Long id) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JobPost not found with id: " + id));
        jobPostRepository.delete(jobPost);
    }
}