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
@Table(name = "Brands", uniqueConstraints = {
    @UniqueConstraint(name = "uk_brand_name", columnNames = {"name"})
})
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brandId;
    
    @Column(nullable = false)
    private String name;
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    private List<Product> products;

    // Change from OneToMany to ManyToOne
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "image_id")
    private Image image;

    // Keep the old field for backward compatibility during migration
//    @ElementCollection
//    @CollectionTable(name = "brand_image_urls",
//            joinColumns = @JoinColumn(name = "brand_image_id"))
//    @Column(name = "image_url")
//    @Deprecated
//    private List<String> imageUrls;
//
//    // Helper methods for backward compatibility
//    public List<String> getImages() {
//        if (this.image == null) {
//            return new ArrayList<>();
//        }
//        List<String> result = new ArrayList<>();
//        result.add(this.image.getImageUrl());
//        return result;
//    }
}
