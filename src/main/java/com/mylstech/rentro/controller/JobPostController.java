package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.JobPostRequest;
import com.mylstech.rentro.dto.response.JobPostResponse;
import com.mylstech.rentro.service.JobPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job-posts")
@RequiredArgsConstructor
public class JobPostController {

    private final JobPostService jobPostService;

    @GetMapping
    public ResponseEntity<List<JobPostResponse>> getAllJobPosts() {
        return ResponseEntity.ok(jobPostService.getAllJobPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostResponse> getJobPostById(@PathVariable Long id) {
        return ResponseEntity.ok(jobPostService.getJobPostById(id));
    }

    @PostMapping
    public ResponseEntity<JobPostResponse> createJobPost(@RequestBody JobPostRequest request) {
        return new ResponseEntity<>(jobPostService.createJobPost(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobPostResponse> updateJobPost(@PathVariable Long id, @RequestBody JobPostRequest request) {
        return ResponseEntity.ok(jobPostService.updateJobPost(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobPost(@PathVariable Long id) {
        jobPostService.deleteJobPost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/updateJobApplicantCount")
    public ResponseEntity<Void> updateJobApplicantsCount(){
        jobPostService.updateJobPostCount();
        return ResponseEntity.noContent ().build ();
    }
}