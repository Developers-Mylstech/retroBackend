package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.ProductFor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductForResponse {
    private Long productForId;
    private SellResponse sell;
    private RentResponse rent;
    private ServiceResponse service;
    private Boolean isAvailableForRequestQuotation;

    public ProductForResponse(ProductFor productFor) {
        this.productForId = productFor.getProductForId();
        
        if (productFor.getSell() != null) {
            this.sell = new SellResponse(productFor.getSell());
        }
        
        if (productFor.getRent() != null) {
            this.rent = new RentResponse(productFor.getRent());
        }

        if (productFor.getServices() != null) {
            this.service = new ServiceResponse(productFor.getServices());
        }
        if (productFor.getIsAvailableForRequestQuotation() != null) {
            this.isAvailableForRequestQuotation = productFor.getIsAvailableForRequestQuotation();
        }
    }
}
