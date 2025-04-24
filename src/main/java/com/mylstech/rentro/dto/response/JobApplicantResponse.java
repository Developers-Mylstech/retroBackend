package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.JobApplicant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicantResponse {
    private Long jobApplicantId;
    private String name;
    private String email;
    private String resume;
    private Long jobPostId;
    private String phone;
    private String jobTitle;

    public JobApplicantResponse(JobApplicant jobApplicant) {
        this.jobApplicantId = jobApplicant.getJobApplicantId();
        this.name = jobApplicant.getName();
        this.email = jobApplicant.getEmail();
        this.resume = jobApplicant.getResume();
        this.phone = jobApplicant.getPhone();
        this.jobPostId = jobApplicant.getJobPost().getJobPostId();
        this.jobTitle = jobApplicant.getJobPost().getJobTitle();
    }
}