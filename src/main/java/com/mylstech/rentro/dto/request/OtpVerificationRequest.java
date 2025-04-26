package com.mylstech.rentro.dto.request;

import lombok.Data;

@Data
public class OtpVerificationRequest {
    private String email;
    private String otp;

}
