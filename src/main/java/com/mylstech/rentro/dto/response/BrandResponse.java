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

    public BrandResponse(Brand brand) {
        this.brandId = brand.getBrandId ( );
        this.name = brand.getName ( );
    }
}
