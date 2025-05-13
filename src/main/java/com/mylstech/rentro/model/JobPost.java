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

    // Changed from OneToOne to ManyToOne with proper cascade settings
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id")
    private Image image;

    @Size(max = 500)
    @Column(name = "image_url")
    @Deprecated
    private String imageUrl;

    // Helper methods for backward compatibility
    /**
     * @deprecated Use getImage().getImageUrl() instead
     * @return the image URL
     */
    @Deprecated
    public String getImageUrl() {
        return this.image != null ? this.image.getImageUrl() : this.imageUrl;
    }

    /**
     * @deprecated Use setImage(Image) instead
     * @param imageUrl the image URL
     */
    @Deprecated
    public void setImageUrl(String imageUrl) {
        // Store in the deprecated field for backward compatibility
        this.imageUrl = imageUrl;
        
        // Don't automatically create Image entities here
        // This should be handled by the service layer
    }
}
