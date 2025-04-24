package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.RequestQuotation;
import com.mylstech.rentro.util.RequestQuotationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestQuotationResponse {
    private Long requestQuotationId;
    private String name;
    private String mobile;
    private String companyName;
    private String location;
    private List<String> productImages;
    private RequestQuotationStatus status;

    public RequestQuotationResponse(RequestQuotation requestQuotation) {
        this.requestQuotationId = requestQuotation.getRequestQuotationId();
        this.name = requestQuotation.getName();
        this.mobile = requestQuotation.getMobile();
        this.companyName = requestQuotation.getCompanyName();
        this.location = requestQuotation.getLocation();
        this.productImages = requestQuotation.getProductImages();
        this.status = requestQuotation.getStatus();
    }
}
