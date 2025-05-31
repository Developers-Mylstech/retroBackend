package com.mylstech.rentro.util;

import com.mylstech.rentro.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailTemplates {

    private final EmailService emailService;

    @Value("${app.email.sender-name:Rentro}")
    private String senderName;

    @Value("${app.url:http://localhost:8080}")
    private String baseUrl;
    /**
 * Send an OTP (One-Time Password) email
 * @param to Recipient email address
 * @param recipientName Name of the recipient
 * @param otp The OTP code to send
 * @param expiryMinutes Number of minutes until the OTP expires
 */
public void sendOtpEmail(String to, String recipientName, String otp, int expiryMinutes) {
    String subject = "Your One-Time Password (OTP) for Rentro";

    // Format OTP with spaces for better readability
    String formattedOtp = String.join(" ", otp.split(""));

    Map<String, Object> templateModel = new HashMap<>();
    templateModel.put("recipientName", recipientName);
    templateModel.put("otp", formattedOtp);
    templateModel.put("expiryMinutes", expiryMinutes);
    templateModel.put("logoUrl", "https://testing.rentro.ae/assets/renroLogo-p3-PWqCh.png");
    templateModel.put("websiteUrl", "https://rentro.ae");
    templateModel.put("supportEmail", "support@rentro.ae");

    // Optional: Add social media links (set to null if not needed)
    templateModel.put("facebookUrl", "https://facebook.com/rentro");
    templateModel.put("twitterUrl", "https://twitter.com/rentro");
    templateModel.put("instagramUrl", "https://instagram.com/rentro");
    templateModel.put("linkedinUrl", "https://linkedin.com/company/rentro");

    emailService.sendHtmlEmail(to, subject, "otp-email", templateModel);
}

    /**
     * Send a welcome email with verification link
     * @param to Recipient email address
     * @param recipientName Name of the recipient
     * @param verificationToken Email verification token
     */
    public void sendWelcomeEmail(String to, String recipientName, String verificationToken) {
        String subject = "Welcome to Rentro - Verify Your Email";
        String verificationLink = baseUrl + "/api/v1/auth/verify-email?token=" + verificationToken;

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("name", recipientName);
        templateModel.put("verificationLink", verificationLink);
        templateModel.put("senderName", senderName);

        emailService.sendHtmlEmail(to, subject, "welcome-email", templateModel);
    }

    /**
     * Send a password reset email
     * @param to Recipient email address
     * @param recipientName Name of the recipient
     * @param resetToken Password reset token
     */
    public void sendPasswordResetEmail(String to, String recipientName, String resetToken) {
        String subject = "Password Reset Request";
        String resetLink = baseUrl + "/reset-password?token=" + resetToken;

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("name", recipientName);
        templateModel.put("resetLink", resetLink);
        templateModel.put("senderName", senderName);

        emailService.sendHtmlEmail(to, subject, "password-reset-email", templateModel);
    }

    /**
     * Send an order confirmation email
     * @param to Recipient email address
     * @param recipientName Name of the recipient
     * @param orderNumber Order number
     * @param orderDetails Details of the order
     */
    public void sendOrderConfirmationEmail(String to, String recipientName, String orderNumber, String orderDetails) {
        String subject = "Order Confirmation - #" + orderNumber;

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("name", recipientName);
        templateModel.put("orderNumber", orderNumber);
        templateModel.put("orderDetails", orderDetails);
        templateModel.put("senderName", senderName);

        emailService.sendHtmlEmail(to, subject, "order-confirmation-email", templateModel);
    }
}
