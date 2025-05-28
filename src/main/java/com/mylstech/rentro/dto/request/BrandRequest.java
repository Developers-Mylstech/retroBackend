package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Brand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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


        return brand;
    }
}
