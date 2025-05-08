package com.mylstech.rentro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OurService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ourServiceId;
    private String title;
    private String shortDescription;
    private String detailedHeading;
    @Size(max = 500)
    private String detailedDescription;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "our_service_id")
    private List<Image> images = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "our_services_image_urls",
            joinColumns = @JoinColumn(name = "our_service_id"))
    @Column(name = "image_url")
    @Deprecated
    private List<String> imageUrl;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feature> feature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
}
