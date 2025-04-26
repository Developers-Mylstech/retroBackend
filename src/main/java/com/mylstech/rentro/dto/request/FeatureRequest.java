package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Feature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureRequest {
    private String title;
    private String description;
    
    public Feature requestToFeature() {
        Feature feature = new Feature();
        feature.setTitle(title);
        feature.setDescription(description);
        return feature;
    }
}
