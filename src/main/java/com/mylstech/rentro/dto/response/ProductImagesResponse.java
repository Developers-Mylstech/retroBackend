package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.ProductImages;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImagesResponse {
    private Long productImageId;
    private Long productId;
    private String productName;
    private List<String> imageUrls;

    public ProductImagesResponse(ProductImages productImages) {
        this.productImageId = productImages.getProductImageId();
        
        if (productImages.getProduct() != null) {
            this.productId = productImages.getProduct().getProductId();
            this.productName = productImages.getProduct().getName();
        }
        
        this.imageUrls = productImages.getImageUrls();
    }
}
