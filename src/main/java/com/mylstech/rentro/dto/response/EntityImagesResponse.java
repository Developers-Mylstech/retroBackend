package com.mylstech.rentro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityImagesResponse {
    private Long entityId;
    private String entityType;
    private List<String> imageUrls;
    private String singleImage; // For entities with single image like JobPost
}