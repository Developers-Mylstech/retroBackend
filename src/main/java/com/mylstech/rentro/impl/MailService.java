package com.mylstech.rentro.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    public void sendOTP(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage ( );
        message.setTo ( toEmail );
        message.setSubject ( "Your OTP Code" );
        message.setText ( "Your OTP is: " + otp );
        javaMailSender.send ( message );
    }
}

