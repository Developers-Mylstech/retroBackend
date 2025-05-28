package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.JobPostRequest;
import com.mylstech.rentro.dto.response.JobPostResponse;
import com.mylstech.rentro.service.JobPostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job-posts")
@RequiredArgsConstructor
public class JobPostController {

    private static final String RESOURCE_NOT_FOUND = "Resource not found: {}";
    private final JobPostService jobPostService;
    private final Logger logger = LoggerFactory.getLogger ( JobPostController.class );

    @GetMapping
    public ResponseEntity<List<JobPostResponse>> getAllJobPosts() {
        return ResponseEntity.ok ( jobPostService.getAllJobPosts ( ) );
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostResponse> getJobPostById(@PathVariable Long id) {
        return ResponseEntity.ok ( jobPostService.getJobPostById ( id ) );
    }

    @PostMapping
    @Operation(summary = "Create job post", description = "Creates a new job post")
    public ResponseEntity<JobPostResponse> createJobPost(@RequestBody JobPostRequest request) {
        logger.info ( "Creating new job post with title: {}", request.getJobTitle ( ) );
        JobPostResponse response = jobPostService.createJobPost ( request );
        logger.info ( "Successfully created job post with ID: {}", response.getJobPostId ( ) );
        return new ResponseEntity<> ( response, HttpStatus.CREATED );

    }

    @PutMapping("/{id}")
    public ResponseEntity<JobPostResponse> updateJobPost(@PathVariable Long id, @RequestBody JobPostRequest request) {
        return ResponseEntity.ok ( jobPostService.updateJobPost ( id, request ) );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobPost(@PathVariable Long id) {

        jobPostService.deleteJobPost ( id );
        return ResponseEntity.noContent ( ).build ( );

    }

    @GetMapping("/updateJobApplicantCount")
    public ResponseEntity<Void> updateJobApplicantsCount() {

        jobPostService.updateJobPostCount ( );
        return ResponseEntity.noContent ( ).build ( );

    }

    @PutMapping("/{id}/image/{imageId}")
    @Operation(summary = "Set job post image", description = "Sets the image for a job post")
    public ResponseEntity<JobPostResponse> setJobPostImage(
            @PathVariable Long id,
            @PathVariable Long imageId) {

        logger.debug ( "Setting image {} for job post {}", imageId, id );
        JobPostResponse jobPost = jobPostService.setJobPostImage ( id, imageId );
        logger.debug ( "Successfully set image for job post" );
        return ResponseEntity.ok ( jobPost );

    }

    @DeleteMapping("/{id}/image")
    @Operation(summary = "Remove job post image", description = "Removes the image from a job post")
    public ResponseEntity<JobPostResponse> removeJobPostImage(@PathVariable Long id) {

        logger.debug ( "Controller: Removing image from job post {}", id );
        JobPostResponse jobPost = jobPostService.removeJobPostImage ( id );
        logger.debug ( "Controller: Successfully removed image from job post" );
        return ResponseEntity.ok ( jobPost );

    }
}
