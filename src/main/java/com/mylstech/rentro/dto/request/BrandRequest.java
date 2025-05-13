package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Brand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandRequest {

    private String name;

//    private List<String> imageUrls; // For backward compatibility
    private Long imageId; // New field for single image

    public Brand requestToBrand() {
        Brand brand = new Brand ( );
        brand.setName ( this.name );

        // Handle imageUrls for backward compatibility
//        if ( this.imageUrls != null && ! this.imageUrls.isEmpty ( ) ) {
//            brand.setImageUrls ( this.imageUrls );
//
////            // Also set the first URL as the main image URL
////            brand.setImageUrl ( this.imageUrls.get ( 0 ) );
//        }

        return brand;
    }
}
