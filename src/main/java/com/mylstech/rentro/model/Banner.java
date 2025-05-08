package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name ="banners")
@NoArgsConstructor
@AllArgsConstructor
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bannerId;
    private String title;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image image;
    @Column(name = "image_url")
    @Deprecated
    private String imageUrl;

    // Helper methods for backward compatibility
    public String getImageUrl() {
        return this.image != null ? this.image.getImageUrl() : null;
    }

    public void setImageUrl(String imageUrl) {
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
