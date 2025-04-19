package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.JobApplicantRequest;
import com.mylstech.rentro.dto.response.JobApplicantResponse;
import com.mylstech.rentro.service.JobApplicantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job-applicants")
@RequiredArgsConstructor
public class JobApplicantController {

    private final JobApplicantService jobApplicantService;

    @GetMapping
    public ResponseEntity<List<JobApplicantResponse>> getAllJobApplicants() {
        return ResponseEntity.ok(jobApplicantService.getAllJobApplicants());
    }

    @GetMapping("/job-post/{jobPostId}")
    public ResponseEntity<List<JobApplicantResponse>> getJobApplicantsByJobPost(@PathVariable Long jobPostId) {
        return ResponseEntity.ok(jobApplicantService.getJobApplicantsByJobPost(jobPostId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicantResponse> getJobApplicantById(@PathVariable Long id) {
        return ResponseEntity.ok(jobApplicantService.getJobApplicantById(id));
    }

    @PostMapping
    public ResponseEntity<JobApplicantResponse> createJobApplicant(@RequestBody JobApplicantRequest request) {
        return new ResponseEntity<>(jobApplicantService.createJobApplicant(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplicantResponse> updateJobApplicant(
            @PathVariable Long id,
            @RequestBody JobApplicantRequest request) {
        return ResponseEntity.ok(jobApplicantService.updateJobApplicant(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobApplicant(@PathVariable Long id) {
        jobApplicantService.deleteJobApplicant(id);
        return ResponseEntity.noContent().build();
    }
}