package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.JobPostRequest;
import com.mylstech.rentro.dto.response.JobPostResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.model.Image;
import com.mylstech.rentro.model.JobPost;
import com.mylstech.rentro.repository.ImageRepository;
import com.mylstech.rentro.repository.JobApplicantRepository;
import com.mylstech.rentro.repository.JobPostRepository;
import com.mylstech.rentro.service.JobPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobPostServiceImpl implements JobPostService {

    private final JobPostRepository jobPostRepository;
    private final JobApplicantRepository jobApplicantRepository;
    private final ImageRepository imageRepository;
    private final Logger logger = LoggerFactory.getLogger(JobPostServiceImpl.class);

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
    @Transactional
    public JobPostResponse createJobPost(JobPostRequest request) {
        JobPost jobPost = request.requestToJobPost();
        jobPost.setJobCode(generateJobCode());
        jobPost.setTotalApplicants(0);
        
        // Handle image if imageId is provided
        if (request.getImageId() != null) {
            Image image = imageRepository.findById(request.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image", "id", request.getImageId()));
            jobPost.setImageUrl (image.getImageUrl ());
        } 
        // For backward compatibility, handle imageUrl if provided
        else if (request.getImage() != null) {
            // Find or create an Image entity for this URL
            Image image = imageRepository.findByImageUrl(request.getImage())
                    .orElseGet(() -> {
                        Image newImage = new Image();
                        newImage.setImageUrl(request.getImage());
                        return imageRepository.save(newImage);
                    });
            jobPost.setImage (image);
            
            // Also set the deprecated field for backward compatibility
            jobPost.setImageUrl(request.getImage());
        }
        
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
    @Transactional
    public JobPostResponse updateJobPost(Long id, JobPostRequest request) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("JobPost", "id", id));

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
        
        // Handle image if imageId is provided
        if (request.getImageId() != null) {
            Image image = imageRepository.findById(request.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image", "id", request.getImageId()));
            
            // Clear previous image reference
            jobPost.setImageUrl (null);
            jobPostRepository.saveAndFlush(jobPost);
            
            // Set new image
            jobPost.setImage (image);
            
            // Also update the deprecated field for backward compatibility
            jobPost.setImageUrl(image.getImageUrl());
        } 
        // For backward compatibility, handle imageUrl if provided
        else if (request.getImage() != null) {
            // Clear previous image reference
            jobPost.setImageUrl (null);
            jobPostRepository.saveAndFlush(jobPost);
            
            // Find or create an Image entity for this URL
            Image image = imageRepository.findByImageUrl(request.getImage())
                    .orElseGet(() -> {
                        Image newImage = new Image();
                        newImage.setImageUrl(request.getImage());
                        return imageRepository.save(newImage);
                    });
            
            jobPost.setImage (image);
            
            // Also set the deprecated field for backward compatibility
            jobPost.setImageUrl(request.getImage());
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

    @Override
    @Transactional
    public JobPostResponse setJobPostImage(Long jobPostId, Long imageId) {
        logger.debug("Setting image {} for job post {}", imageId, jobPostId);
        
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new ResourceNotFoundException("JobPost", "id", jobPostId));
        
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));
        
        logger.debug("Found job post: {}, image: {}", jobPost.getJobPostId(), image.getImageId());
        
        // Clear previous image reference
        Image oldImage = jobPost.getImage ();
        if (oldImage != null) {
            logger.debug("Clearing old image: {}", oldImage.getImageId());
        }
        
        // Clear the image reference first
        jobPost.setImageUrl (null);
        jobPostRepository.saveAndFlush(jobPost);
        logger.debug("Cleared old image reference");
        
        // Check if this image is already used by another job post
        List<JobPost> jobPostsWithImage = jobPostRepository.findByImageImageId(imageId);
        for (JobPost existingJobPost : jobPostsWithImage) {
            if (!existingJobPost.getJobPostId().equals(jobPostId)) {
                logger.debug("Image {} is already used by job post {}. Creating a copy.", 
                        imageId, existingJobPost.getJobPostId());
                
                // Create a copy of the image
                Image imageCopy = new Image();
                imageCopy.setImageUrl(image.getImageUrl());
                image = imageRepository.save(imageCopy);
                logger.debug("Created image copy with ID: {}", image.getImageId());
                break;
            }
        }
        
        // Set the new image
        jobPost.setImage(image);
        
        // Also update the deprecated field for backward compatibility
        jobPost.setImageUrl(image.getImageUrl());
        
        JobPost savedJobPost = jobPostRepository.save(jobPost);
        logger.debug("Set new image and saved job post");
        
        return new JobPostResponse(savedJobPost);
    }
    
    @Override
    @Transactional
    public JobPostResponse removeJobPostImage(Long jobPostId) {
        logger.debug("Removing image from job post {}", jobPostId);
        
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new ResourceNotFoundException("JobPost", "id", jobPostId));
        
        // Log the current state
        if (jobPost.getImage() != null) {
            logger.debug("Current image ID: {}, URL: {}", 
                    jobPost.getImage().getImageId(), 
                    jobPost.getImage().getImageUrl());
        } else {
            logger.debug("No image currently associated with job post");
        }
        
        try {
            // First approach: Use JPA entity
            jobPost.setImage(null);
            jobPost.setImageUrl(null);
            
            // Save and flush to ensure changes are committed immediately
            JobPost savedJobPost = jobPostRepository.saveAndFlush(jobPost);
            
            // Verify the image was cleared
            if (savedJobPost.getImage() == null) {
                logger.debug("Successfully cleared image from job post using JPA");
            } else {
                logger.warn("Failed to clear image using JPA, trying native query...");
                
                // Second approach: Use native query as fallback
                jobPostRepository.clearJobPostImage(jobPostId);
                
                // Refresh the entity from the database
                jobPostRepository.flush();
                savedJobPost = jobPostRepository.findById(jobPostId)
                        .orElseThrow(() -> new ResourceNotFoundException("JobPost", "id", jobPostId));
                
                if (savedJobPost.getImage() == null) {
                    logger.debug("Successfully cleared image using native query");
                } else {
                    logger.error("Failed to clear image even with native query!");
                }
            }
            
            return new JobPostResponse(savedJobPost);
        } catch (Exception e) {
            logger.error("Error while removing image from job post", e);
            throw e;
        }
    }
}
