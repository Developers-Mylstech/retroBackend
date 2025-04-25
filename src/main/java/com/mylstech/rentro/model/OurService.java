package com.mylstech.rentro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @ElementCollection
    @CollectionTable(name = "our_services_image_urls",
            joinColumns = @JoinColumn(name = "our_service_id"))
    @Column(name = "image_url")
    private List<String> imageUrl;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feature> feature;
}
