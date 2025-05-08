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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "about_us")
public class AboutUs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aboutUsId;
    
    private String title;
    
    private String subtitle;
    
    @Size(max = 1000)
    @Column(length = 1000)
    private String description;
    
    // Old field for backward compatibility
    @Column(name = "image_url")
    private String imageUrl;
    
    // New field for Image entity integration with ManyToOne relationship
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id")
    private Image image;
}
