package com.igsm.chatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("istgsm@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true = isHtml

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error sending email: " + e.getMessage(), e);
        }
    }

    public void sendBulkEmail(List<String> recipients, String subject, String body) {
        // Run asynchronously to avoid blocking the API
        CompletableFuture.runAsync(() -> {
            for (String to : recipients) {
                if (to != null && !to.isEmpty() && to.contains("@")) {
                    try {
                        sendEmail(to, subject, body);
                        // Avoid rate limits
                        Thread.sleep(500);
                    } catch (Exception e) {
                        System.err.println("Failed to send email to " + to + ": " + e.getMessage());
                    }
                }
            }
        });
    }
}
