package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.JobPost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostRequest {
    private String jobTitle;
    private String jobDescription;
    private String requirements;
    private Boolean isActive;
    

    
    private Long imageId;

    public JobPost requestToJobPost() {
        JobPost jobPost = new JobPost();
        jobPost.setJobTitle(jobTitle);
        jobPost.setJobDescription(jobDescription);
        jobPost.setRequirements(requirements);
        jobPost.setIsActive(isActive);
        // Image will be set separately in the service
        return jobPost;
    }
}