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
    private List<String> imageUrls;

    public Brand requestToBrand() {
        Brand brand = new Brand ( );
        brand.setName ( name );
        brand.setImages ( imageUrls );
        return brand;
    }
}
