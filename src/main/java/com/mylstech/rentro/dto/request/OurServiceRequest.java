package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Feature;
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
public class OurServiceRequest {
    private String title;
    private String shortDescription;
    private String detailedHeading;
    private String detailedDescription;
    
    // Changed from List<String> to String
    @Deprecated
    private String imageUrl;

    private Long imageId;
    
    private List<FeatureRequest> features;
    
    public OurService requestToOurService() {
        OurService ourService = new OurService();
        ourService.setTitle(this.title);
        ourService.setShortDescription(this.shortDescription);
        ourService.setDetailedHeading(this.detailedHeading);
        ourService.setDetailedDescription(this.detailedDescription);
        ourService.setImageUrl(this.imageUrl);
        
        if (this.features != null) {
            List<Feature> featureList = this.features.stream()
                    .map(featureRequest -> {
                        Feature feature = new Feature();
                        feature.setTitle(featureRequest.getTitle());
                        feature.setDescription(featureRequest.getDescription());
                        return feature;
                    })
                    .toList();
            ourService.setFeature(featureList);
        }
        
        return ourService;
    }
}
