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
    public Brand requestToBrand() {
        Brand brand = new Brand ( );
        brand.setName ( name );
        return brand;
    }
}
