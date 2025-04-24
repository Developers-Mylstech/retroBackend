package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.model.RequestQuotation;
import com.mylstech.rentro.util.RequestQuotationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestQuotationRequest {
    private String name;
    private String mobile;
    private String companyName;
    private String location;
    private List<String> productImages;
    private RequestQuotationStatus status;
    
    public RequestQuotation requestToRequestQuotation() {
        RequestQuotation requestQuotation = new RequestQuotation();
        requestQuotation.setCompanyName (companyName);
        requestQuotation.setName (name);
        requestQuotation.setMobile (mobile);
        requestQuotation.setLocation (location);
        requestQuotation.setProductImages (productImages);
        requestQuotation.setStatus (status);
        return requestQuotation;
    }
}
