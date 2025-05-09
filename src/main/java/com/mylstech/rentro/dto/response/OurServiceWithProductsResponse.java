package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.OurService;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class OurServiceWithProductsResponse extends OurServiceResponse {
    private List<ProductResponse> relatedProducts;
    
    public OurServiceWithProductsResponse(OurService ourService) {
        super(ourService);
        
        if (ourService.getProducts() != null && !ourService.getProducts().isEmpty()) {
            this.relatedProducts = ourService.getProducts().stream()
                    .map(ProductResponse::new)
                    .collect(Collectors.toList());
        } else {
            this.relatedProducts = new ArrayList<>();
        }
    }
}