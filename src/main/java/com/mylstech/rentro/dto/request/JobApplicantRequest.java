package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.JobApplicant;
import com.mylstech.rentro.model.JobPost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicantRequest {
    private String name;
    private String email;
    private String resume;
    private String phone;
    private Long jobPostId;

    public JobApplicant requestToJobApplicant(JobPost jobPost) {
        JobApplicant jobApplicant = new JobApplicant();
        jobApplicant.setName(name);
        jobApplicant.setEmail(email);
        jobApplicant.setResume(resume);
        jobApplicant.setPhone(phone);
        jobApplicant.setJobPost(jobPost);
        return jobApplicant;
    }
}