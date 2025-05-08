package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;
    
    @Column(nullable = false)
    private String imageUrl;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    // Helper method to extract file path from URL for deletion
    public String getFilePath() {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
}
