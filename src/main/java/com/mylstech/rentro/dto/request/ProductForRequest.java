package com.mylstech.rentro.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for ProductFor information in product requests.
 * Contains either IDs for existing entities or full request objects for creating new entities.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductForRequest {
    // IDs for existing entities


    // Full request objects for creating new entities
    private SellRequest sell;
    private RentRequest rent;
    private RequestQuotationRequest requestQuotation;
    private ServiceRequest service;
}
