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
//    private List<String> imageUrl;
    private String imageUrl;
    private List<FeatureRequest> features;
    
    public OurService requestToOurService() {
        OurService ourService = new OurService();
        ourService.setTitle(title);
        ourService.setShortDescription(shortDescription);
        ourService.setDetailedHeading(detailedHeading);
        ourService.setDetailedDescription(detailedDescription);
//        ourService.setImageUrl(imageUrl != null ? imageUrl : new ArrayList<>());
        ourService.setImageUrl ( imageUrl );
        if (features != null && !features.isEmpty()) {
            List<Feature> featureList = features.stream()
                    .map(FeatureRequest::requestToFeature)
                    .collect(Collectors.toList());
            ourService.setFeature(featureList);
        } else {
            ourService.setFeature(new ArrayList<>());
        }
        
        return ourService;
    }
}
