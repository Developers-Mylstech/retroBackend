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
    private String jobDescription;
    private String requirements;
    private Boolean isActive;
    @Column(columnDefinition = "integer default 0")
    private Integer totalApplicants;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image image;

    @Size(max = 500)
    @Column(name = "image_url")
    @Deprecated
    private String imageUrl;

    // Helper methods for backward compatibility
    public String getImage() {
        return this.image != null ? this.image.getImageUrl() : null;
    }

    public void setImage(String imageUrl) {
        if (imageUrl == null) {
            this.image = null;
            return;
        }
        
        if (this.image == null) {
            this.image = new Image();
        }
        this.image.setImageUrl(imageUrl);
    }
}
