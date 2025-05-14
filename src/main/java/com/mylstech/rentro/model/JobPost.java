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

    @Column(unique = true)
    private String jobCode;

    private String jobTitle;
    
    @Column(columnDefinition = "TEXT")
    private String jobDescription;
    
    @Column(columnDefinition = "TEXT")
    private String requirements;
    
    private Boolean isActive;
    
    @Column(columnDefinition = "integer default 0")
    private Integer totalApplicants;

    // Changed from OneToOne to ManyToOne with proper cascade settings
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id")
    private Image image;
//    @Deprecated
//    @Column(name = "image_url", length = 500)
//    private String imageUrl;
    
    // Helper method for backward compatibility
//    public String getImage() {
//        if (this.image != null && this.image.getImageUrl() != null) {
//            return this.image.getImageUrl();
//        }
//        return this.imageUrl;
//    }
}
