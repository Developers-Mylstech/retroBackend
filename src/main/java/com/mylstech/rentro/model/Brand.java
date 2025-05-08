package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brandId;
    private String name;
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    private List<Product> products;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "brand_id")
    private List<Image> images = new ArrayList<> ();

    // Keep the old field for backward compatibility during migration
    @ElementCollection
    @CollectionTable(name = "brand_image_urls",
            joinColumns = @JoinColumn(name = "brand_image_id"))
    @Column(name = "image_url")
    @Deprecated
    private List<String> imageUrls;

    // Helper methods for backward compatibility
    public List<String> getImages() {
        if (this.images == null || this.images.isEmpty()) {
            return new ArrayList<>();
        }
        return this.images.stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());
    }

    public void setImages(List<String> urls) {
        if (urls == null) {
            return;
        }
        
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        
        // Convert URLs to Image entities
        for (String url : urls) {
            Image image = new Image();
            image.setImageUrl(url);
            this.images.add(image);
        }
    }

    // Helper method to add an image
    public void addImage(Image image) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        this.images.add(image);
    }
}
