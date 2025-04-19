package com.mylstech.rentro.service;

public interface OtpService {
    String generateOTP(String email);

    boolean verifyOTP(String email, String otp);

    void removeOTP(String email);
}
