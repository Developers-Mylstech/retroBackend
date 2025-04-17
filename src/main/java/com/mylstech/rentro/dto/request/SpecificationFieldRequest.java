package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.SpecificationField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecificationFieldRequest {
    private String name;
    
    public SpecificationField requestToSpecificationField() {
        SpecificationField specificationField = new SpecificationField();
        specificationField.setName(name);
        return specificationField;
    }
}
