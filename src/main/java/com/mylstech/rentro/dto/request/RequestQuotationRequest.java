package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.RequestQuotation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestQuotationRequest {
    private Double actualPrice;
    private Double discountPrice;

    
    public RequestQuotation requestToRequestQuotation() {
        RequestQuotation requestQuotation = new RequestQuotation();
        requestQuotation.setActualPrice(actualPrice);
        requestQuotation.setDiscountPrice(discountPrice);
        return requestQuotation;
    }
}
