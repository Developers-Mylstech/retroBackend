package com.mylstech.rentro.impl;

import com.mylstech.rentro.service.EmailService;
import com.mylstech.rentro.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OtpService {
    
    private final EmailService emailService;
    
    @Value("${otp.expiry.minutes:5}")
    private int otpExpiryMinutes;
    
    // Store OTPs with expiry time: <identifier, <otp, expiryTime>>
    private final Map<String, Map.Entry<String, LocalDateTime>> otpStorage = new ConcurrentHashMap<>();
    
    @Override
    public String generateOTP(String email) {
        String otp = generateSecureOtp();
        System.out.println("---------------> otp is here "+otp);
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(otpExpiryMinutes);
        
        // Store OTP with expiry time
        otpStorage.put(email, new SimpleEntry<>(otp, expiryTime));
        // Send OTP via email
        sendOtpViaEmail(email, otp);
        
        return otp;
    }
    
    @Override
    public boolean verifyOTP(String email, String otp) {
        Map.Entry<String, LocalDateTime> storedOtpEntry = otpStorage.get(email);
        
        if (storedOtpEntry == null) {
            return false;
        }
        
        String storedOtp = storedOtpEntry.getKey();
        LocalDateTime expiryTime = storedOtpEntry.getValue();
        
        // Check if OTP is valid and not expired
        boolean isValid = storedOtp.equals(otp) && LocalDateTime.now().isBefore(expiryTime);
        
        // Remove OTP after successful verification
        if (isValid) {
            removeOTP(email);
        }
        
        return isValid;
    }
    
    @Override
    public void removeOTP(String email) {
        otpStorage.remove(email);
    }
    
    private String generateSecureOtp() {
        SecureRandom random = new SecureRandom();
        // Generate a 6-digit OTP (100000-999999)
        return String.valueOf(random.nextInt(900000) + 100000);
    }
    
    private void sendOtpViaEmail(String email, String otp) {
        String subject = "Your OTP for Login";
        String body = "Your OTP for login is: " + otp + ". It will expire in " + otpExpiryMinutes + " minutes.";
        
        emailService.sendEmail(email, subject, body);
    }
}

