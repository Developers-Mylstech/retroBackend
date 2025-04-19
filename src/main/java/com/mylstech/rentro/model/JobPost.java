package com.mylstech.rentro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "job_posts")
public class JobPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobPostId;
    
    private String jobTitle;
    private String jobDescription;
    private String requirements;
    private Boolean isActive;
    @Size(max = 500)
    private String image;
}