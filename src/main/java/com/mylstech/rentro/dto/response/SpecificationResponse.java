package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Specification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecificationResponse {
    private Long specificationId;
    private String name;
    private String value;

    public SpecificationResponse(Specification specification) {
        this.specificationId = specification.getSpecificationId();
        this.name = specification.getName();
        this.value = specification.getValue();
    }
}
