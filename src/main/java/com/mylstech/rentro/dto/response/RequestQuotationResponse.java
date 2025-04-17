package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.RequestQuotation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestQuotationResponse {
    private Long requestQuotationId;
    private Double actualPrice;
    private Double discountPrice;
    private Double vat;

    public RequestQuotationResponse(RequestQuotation requestQuotation) {
        this.requestQuotationId = requestQuotation.getRequestQuotationId();
        this.actualPrice = requestQuotation.getActualPrice();
        this.discountPrice = requestQuotation.getDiscountPrice();
        this.vat = requestQuotation.getVat();
    }
}
