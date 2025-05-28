package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Brand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponse {
    private Long brandId;
    private String name;
    private ImageDTO image; // New field for single image

    public BrandResponse(Brand brand) {
        this.brandId = brand.getBrandId ( );
        this.name = brand.getName ( );


        if ( brand.getImage ( ) != null ) {
            this.image = new ImageDTO ( brand.getImage ( ) );

        }

    }
}
