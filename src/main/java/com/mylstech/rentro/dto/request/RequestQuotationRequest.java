package com.mylstech.rentro.dto.request;

import com.mylstech.rentro.dto.response.EntityImagesResponse;
import com.mylstech.rentro.dto.response.ImageDTO;
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
    private LocationRequest location;
    private ImageDTO image;
    private RequestQuotationStatus status;
    
    public RequestQuotation requestToRequestQuotation() {
        RequestQuotation requestQuotation = new RequestQuotation();
        requestQuotation.setCompanyName (companyName);
        requestQuotation.setName (name);
        requestQuotation.setMobile (mobile);
        requestQuotation.setStatus (RequestQuotationStatus.SENT_QUOTATION);
        return requestQuotation;
    }
}
