package com.mylstech.rentro.impl;

import com.mylstech.rentro.service.OtpService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPServiceImpl implements OtpService {
    private final Map<String, String> otpStorage = new ConcurrentHashMap<> ( );

    @Override
    public String generateOTP(String email) {
        String otp = String.valueOf ( new Random ( ).nextInt ( 899999 ) + 100000 );
        otpStorage.put ( email, otp );
        return otp;
    }

    @Override
    public boolean verifyOTP(String email, String otp) {
        return otp.equals ( otpStorage.get ( email ) );
    }

    @Override
    public void removeOTP(String email) {
        otpStorage.remove ( email );
    }
}

