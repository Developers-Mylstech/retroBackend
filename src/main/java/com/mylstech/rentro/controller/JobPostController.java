package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.JobPostRequest;
import com.mylstech.rentro.dto.response.JobPostResponse;
import com.mylstech.rentro.service.JobPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.dto.response.ErrorResponse;

@RestController
@RequestMapping("/api/v1/job-posts")
@RequiredArgsConstructor
public class JobPostController {

    private final JobPostService jobPostService;
    private final Logger logger = LoggerFactory.getLogger(JobPostController.class);

    @GetMapping
    public ResponseEntity<List<JobPostResponse>> getAllJobPosts() {
        return ResponseEntity.ok(jobPostService.getAllJobPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobPostById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(jobPostService.getJobPostById(id));
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving job post: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve job post: " + e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Create job post", description = "Creates a new job post")
    public ResponseEntity<?> createJobPost(@RequestBody JobPostRequest request) {
        try {
            logger.info("Creating new job post with title: {}", request.getJobTitle());
            JobPostResponse response = jobPostService.createJobPost(request);
            logger.info("Successfully created job post with ID: {}", response.getJobPostId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating job post: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to create job post: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJobPost(@PathVariable Long id, @RequestBody JobPostRequest request) {
        try {
            return ResponseEntity.ok(jobPostService.updateJobPost(id, request));
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating job post: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update job post: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJobPost(@PathVariable Long id) {
        try {
            jobPostService.deleteJobPost(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting job post: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete job post: " + e.getMessage()));
        }
    }

    @GetMapping("/updateJobApplicantCount")
    public ResponseEntity<?> updateJobApplicantsCount() {
        try {
            jobPostService.updateJobPostCount();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error updating job applicant count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update job applicant count: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/image/{imageId}")
    @Operation(summary = "Set job post image", description = "Sets the image for a job post")
    public ResponseEntity<?> setJobPostImage(
            @PathVariable Long id,
            @PathVariable Long imageId) {
        try {
            logger.debug("Setting image {} for job post {}", imageId, id);
            JobPostResponse jobPost = jobPostService.setJobPostImage(id, imageId);
            logger.debug("Successfully set image for job post");
            return ResponseEntity.ok(jobPost);
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (Exception e) {
            logger.error("Error setting image for job post: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to set image: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/image")
    @Operation(summary = "Remove job post image", description = "Removes the image from a job post")
    public ResponseEntity<?> removeJobPostImage(@PathVariable Long id) {
        try {
            logger.debug("Controller: Removing image from job post {}", id);
            JobPostResponse jobPost = jobPostService.removeJobPostImage(id);
            logger.debug("Controller: Successfully removed image from job post");
            
            // Verify the response doesn't have an image
//            if (jobPost.getImage() == null && jobPost.getImageDetails() == null) {
//                logger.debug("Controller: Verified image is null in response");
//            } else {
//                logger.warn("Controller: Image is still present in response!");
//            }
            
            return ResponseEntity.ok(jobPost);
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (Exception e) {
            logger.error("Error removing image from job post: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to remove image: " + e.getMessage()));
        }
    }
}
