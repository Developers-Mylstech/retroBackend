package com.mylstech.rentro.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest {
    private Long otsId;
    private Long mmcId;
    private Long amcBasicId;
    private Long amcGoldId;
}
