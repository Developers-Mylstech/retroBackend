package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.RequestQuotation;
import com.mylstech.rentro.util.RequestQuotationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestQuotationResponse {
    private Long requestQuotationId;
    private String requestQuotationCode;
    private String name;
    private String mobile;
    private String companyName;
    private LocationResponse location;
    private ImageDTO image;
    private RequestQuotationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RequestQuotationResponse(RequestQuotation requestQuotation) {
        this.requestQuotationId = requestQuotation.getRequestQuotationId ( );
        this.requestQuotationCode = requestQuotation.getRequestQuotationCode ( );
        this.name = requestQuotation.getName ( );
        this.mobile = requestQuotation.getMobile ( );
        this.companyName = requestQuotation.getCompanyName ( );

        if ( requestQuotation.getLocation ( ) != null ) {
            this.location = new LocationResponse ( requestQuotation.getLocation ( ) );
        }

        if ( requestQuotation.getImage ( ) != null ) {
            this.image = new ImageDTO (
                    requestQuotation.getImage ( ).getImageId ( ),
                    requestQuotation.getImage ( ).getImageUrl ( )
            );
        }


        this.status = requestQuotation.getStatus ( );
        this.createdAt = requestQuotation.getCreatedAt ( );
        this.updatedAt = requestQuotation.getUpdatedAt ( );
    }
}