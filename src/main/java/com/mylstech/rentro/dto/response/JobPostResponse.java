package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.JobPost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostResponse {
    private Long jobPostId;
    private String jobTitle;
    private String jobDescription;
    private String requirements;
    private Boolean isActive;
    private String image;

    public JobPostResponse(JobPost jobPost) {
        this.jobPostId = jobPost.getJobPostId();
        this.jobTitle = jobPost.getJobTitle();
        this.jobDescription = jobPost.getJobDescription();
        this.requirements = jobPost.getRequirements();
        this.isActive = jobPost.getIsActive();
        this.image = jobPost.getImage();
    }
}