package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.SpecificationField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecificationFieldResponse {
    private Long specificationFieldId;
    private String name;

    public SpecificationFieldResponse(SpecificationField specificationField) {
        this.specificationFieldId = specificationField.getSpecificationFieldId();
        this.name = specificationField.getName();
    }
}
