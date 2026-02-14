package com.igsm.chatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("istgsm@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
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
