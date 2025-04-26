package com.mylstech.rentro.service;

public interface EmailService {
    /**
     * Send an email to the specified recipient
     * @param to Recipient email address
     * @param subject Email subject
     * @param body Email body content
     */
    void sendEmail(String to, String subject, String body);
}