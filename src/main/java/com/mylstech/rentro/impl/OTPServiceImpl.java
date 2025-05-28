package com.mylstech.rentro.impl;

import com.mylstech.rentro.service.EmailService;
import com.mylstech.rentro.service.OtpService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class OTPServiceImpl implements OtpService {
    
    private final EmailService emailService;
    
    @Value("${otp.expiry.minutes:5}")
    private int otpExpiryMinutes;

    @Value("${twilio.account-sid}")
    private String twilioAccountSid;
    @Value("${twilio.auth-token}")
    private String twilioAuthToken;
    @Value("${twilio.from-phone-number}")
    private String twilioFromPhoneNumber;
    
    // Store OTPs with expiry time: <identifier, <otp, expiryTime>>
    private final Map<String, Map.Entry<String, LocalDateTime>> otpStorage = new ConcurrentHashMap<>();
    
    @Override
    public String generateOTPViaEmail(String email) {
        String otp = generateSecureOtp();
       log.info("---------------> otp is here "+otp);
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

    @Override
    public String generateOTPViaPhoneNo(String phone) {
        String otp = generateSecureOtp();
        log.info("---------------> otp is here "+otp);
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(otpExpiryMinutes);

        // Store OTP with expiry time
        otpStorage.put(phone, new SimpleEntry<>(otp, expiryTime));
        // Send OTP via email
        sendOtpViaPhoneNo(phone, otp);

        return otp;
    }

    private void sendOtpViaPhoneNo(String phone, String otp) {
        Twilio.init(twilioAccountSid,twilioAuthToken);
        Message.creator(
                new com.twilio.type.PhoneNumber(phone),
                new com.twilio.type.PhoneNumber("+91"+twilioFromPhoneNumber),
                "Your OTP for login is: " + otp + ". It will expire in " + otpExpiryMinutes + " minutes."
        ).create();
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

