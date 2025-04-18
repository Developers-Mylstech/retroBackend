package com.mylstech.rentro.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImagesRequest {
    private Long productId;
    private List<String> imageUrls;
}
