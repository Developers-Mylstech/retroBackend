package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Brand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponse {
    private Long brandId;
    private String name;
    private List<String> images;

    public BrandResponse(Brand brand) {
        this.brandId = brand.getBrandId ( );
        this.name = brand.getName ( );
        this.images = brand.getImages ();
    }
}
