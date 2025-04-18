package com.mylstech.rentro.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Service information in product requests.
 * Contains request objects for creating new ServiceField entities.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest {
    // ServiceField request objects
    private ServiceFieldRequest ots;
    private ServiceFieldRequest mmc;
    private ServiceFieldRequest amcBasic;
    private ServiceFieldRequest amcGold;
}
