package com.mylstech.rentro.impl;


import com.mylstech.rentro.exception.EmailSendingException;
import com.mylstech.rentro.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;


    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new EmailSendingException ("Failed to send email", e);
        }
    }

    @Override
    @Async
    public void sendHtmlEmailAsync(String to, String subject, String templateName, Map<String, Object> templateModel) {
        try {
            // Prepare the evaluation context
            Context context = new Context();
            context.setVariables(templateModel);

            // Process the HTML template with Thymeleaf
            String htmlContent = templateEngine.process(templateName, context);

            // Create a MIME message
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set email details
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = isHtml
            ClassPathResource image = new ClassPathResource("static/rentro.png");
            helper.addInline("logoImage", image);
            // Send the email
            mailSender.send(message);
            log.info("HTML email sent successfully to: {}", to);

        } catch ( MessagingException e) {
            log.error("Failed to send HTML email to: {}", to, e);
            throw new EmailSendingException("Failed to send HTML email", e);
        } catch (Exception e) {
            log.error("Unexpected error while sending email to: {}", to, e);
            throw new EmailSendingException("Unexpected error while sending email", e);
        }
    }
}