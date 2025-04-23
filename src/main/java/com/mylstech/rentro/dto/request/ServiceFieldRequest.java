package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.ServiceField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceFieldRequest {
    private Double price;
    private List<String> benefits;
    private Integer limitedTimePeriods;
    
    public ServiceField requestToServiceField() {
        ServiceField serviceField = new ServiceField();
        serviceField.setPrice(price);
        serviceField.setLimitedTimePeriods(limitedTimePeriods);
        serviceField.setBenefits(benefits != null ? benefits : new ArrayList<> ());
        return serviceField;
    }
}
