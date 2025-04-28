package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.ServiceField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceFieldResponse {
    private Long serviceFieldId;
    private Double price;
    private List<String> benefits;
    private Integer limitedTimePeriods;
    public ServiceFieldResponse(ServiceField serviceField) {
        this.serviceFieldId = serviceField.getServiceFieldId();
        this.price = serviceField.getPrice();
        this.benefits = serviceField.getBenefits();
        this.limitedTimePeriods = serviceField.getLimitedTimePeriods();
    }
}
