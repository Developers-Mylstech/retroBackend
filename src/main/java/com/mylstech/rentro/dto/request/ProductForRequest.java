package com.mylstech.rentro.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductForRequest {
    private Long sellId;
    private Long rentId;
    private Long requestQuotationId;
    private Long serviceId;
}
