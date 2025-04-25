package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.OurService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<String> imageUrl;
    private List<FeatureResponse> features;
    
    public OurServiceResponse(OurService ourService) {
        this.ourServiceId = ourService.getOurServiceId();
        this.title = ourService.getTitle();
        this.shortDescription = ourService.getShortDescription();
        this.detailedHeading = ourService.getDetailedHeading();
        this.detailedDescription = ourService.getDetailedDescription();
        this.imageUrl = ourService.getImageUrl();
        
        if (ourService.getFeature() != null && !ourService.getFeature().isEmpty()) {
            this.features = ourService.getFeature().stream()
                    .map(FeatureResponse::new)
                    .collect(Collectors.toList());
        }
    }
}
