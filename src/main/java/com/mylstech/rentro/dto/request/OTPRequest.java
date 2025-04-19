package com.mylstech.rentro.dto.request;

import lombok.Data;

@Data
public class OTPRequest {
    private String email;
    private String otp;
}
