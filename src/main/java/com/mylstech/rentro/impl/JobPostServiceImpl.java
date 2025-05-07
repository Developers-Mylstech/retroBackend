package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.JobPostRequest;
import com.mylstech.rentro.dto.response.JobPostResponse;
import com.mylstech.rentro.model.JobPost;
import com.mylstech.rentro.repository.JobApplicantRepository;
import com.mylstech.rentro.repository.JobPostRepository;
import com.mylstech.rentro.service.JobPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobPostServiceImpl implements JobPostService {

    private final JobPostRepository jobPostRepository;
    private final JobApplicantRepository jobApplicantRepository;

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
        jobPost.setJobCode(generateJobCode());
        jobPost.setTotalApplicants(0);
        return new JobPostResponse(jobPostRepository.save(jobPost));
    }

    private String generateJobCode() {
        // Get current year and month
        LocalDate now = LocalDate.now();
        String yearMonth = String.format("%02d%02d", now.getYear() % 100, now.getMonthValue());

        // Find the latest job code for this month
        String prefix = "job" + yearMonth;
        String latestCode = jobPostRepository.findLatestJobCodeByPrefix(prefix);

        int sequence = 1;
        if (latestCode != null) {
            // Extract the sequence number from the latest code
            try {
                sequence = Integer.parseInt(latestCode.substring(7)) + 1;
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                // If parsing fails, start from 1
                sequence = 1;
            }
        }

        // Format: jobYYMM0001
        return String.format("%s%04d", prefix, sequence);
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

    @Override
    public void updateJobPostCount() {
        List<JobPost> jobPosts = jobPostRepository.findAll();
        for (JobPost jobPost : jobPosts) {
            // Count applicants for this job post
            int applicantCount = jobApplicantRepository.countByJobPostJobPostId(jobPost.getJobPostId());
            // Update the job post with the correct count
            jobPost.setTotalApplicants(applicantCount);
        }
        // Save all updated job posts
        jobPostRepository.saveAll(jobPosts);
    }
}