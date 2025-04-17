package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.Specification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecificationRequest {
    private String name;
    private String value;
    
    public Specification requestToSpecification() {
        Specification specification = new Specification();
        specification.setName(name);
        specification.setValue(value);
        return specification;
    }
}
