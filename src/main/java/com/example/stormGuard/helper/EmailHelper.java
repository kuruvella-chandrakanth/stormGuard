package com.example.stormGuard.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailHelper {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.sender.email}")
    private String sender;

    public void sendEmailToUser(String to, String subject, String body, String ccEmail) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(sender);
            mail.setTo(to);
            if (ccEmail != null && !ccEmail.trim().isEmpty()) {
                mail.setCc(ccEmail);
            }
            mail.setSubject(subject);
            mail.setText(body);
            mailSender.send(mail);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email to " + to + ": " + e.getMessage(), e);
        }
    }

    public void testConnection() {
        try {
            ((org.springframework.mail.javamail.JavaMailSenderImpl) mailSender).testConnection();
        } catch (Exception e) {
            throw new RuntimeException("Email connection failed: " + e.getMessage(), e);
        }
    }
}
