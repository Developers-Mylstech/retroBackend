package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Feature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureResponse {
    private Long featureId;
    private String title;
    private String description;
    
    public FeatureResponse(Feature feature) {
        this.featureId = feature.getFeatureId();
        this.title = feature.getTitle();
        this.description = feature.getDescription();
    }
}
