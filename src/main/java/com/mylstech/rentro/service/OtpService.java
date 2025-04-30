package com.mylstech.rentro.service;

public interface OtpService {
    String generateOTPViaEmail(String email);

    boolean verifyOTP(String email, String otp);

    void removeOTP(String email);

    String generateOTPViaPhoneNo(String phone);
}
