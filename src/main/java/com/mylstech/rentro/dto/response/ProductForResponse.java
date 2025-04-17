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
    private RequestQuotationResponse requestQuotation;
    private ServiceResponse service;

    public ProductForResponse(ProductFor productFor) {
        this.productForId = productFor.getProductForId();
        
        if (productFor.getSell() != null) {
            this.sell = new SellResponse(productFor.getSell());
        }
        
        if (productFor.getRent() != null) {
            this.rent = new RentResponse(productFor.getRent());
        }
        
        if (productFor.getRequestQuotation() != null) {
            this.requestQuotation = new RequestQuotationResponse(productFor.getRequestQuotation());
        }
        
        if (productFor.getServices() != null) {
            this.service = new ServiceResponse(productFor.getServices());
        }
    }
}
