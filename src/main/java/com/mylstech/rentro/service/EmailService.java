package com.mylstech.rentro.service;

import java.util.Map;

public interface EmailService {
    /**
     * Send an email to the specified recipient
     * @param to Recipient email address
     * @param subject Email subject
     * @param body Email body content
     */
    void sendEmail(String to, String subject, String body);

    /**
     * Send an HTML email using a Thymeleaf template
     * @param to Recipient email address
     * @param subject Email subject
     * @param templateName Name of the Thymeleaf template (without .html extension)
     * @param templateModel Model containing the variables to be used in the template
     */
    void sendHtmlEmailAsync(String to, String subject, String templateName, Map<String, Object> templateModel);

}