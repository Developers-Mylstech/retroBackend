package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.OurService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OurServiceResponse {
    private Long ourServiceId;
    private String title;
    private String shortDescription;
    private String detailedHeading;
    private String detailedDescription;
    private String imageUrl;
    private ImageDTO image;
    private List<FeatureResponse> features;

    public OurServiceResponse(OurService ourService) {
        this.ourServiceId = ourService.getOurServiceId();
        this.title = ourService.getTitle();
        this.shortDescription = ourService.getShortDescription();
        this.detailedHeading = ourService.getDetailedHeading();
        this.detailedDescription = ourService.getDetailedDescription();
        this.imageUrl = ourService.getImageUrl();
        
        // Safely handle null image
        if (ourService.getImage() != null) {
            this.image = new ImageDTO(ourService.getImage());
        }
        
        if (ourService.getFeature() != null) {
            this.features = ourService.getFeature().stream()
                    .map(FeatureResponse::new)
                    .collect(Collectors.toList());
        } else {
            this.features = new ArrayList<>();
        }
    }
}