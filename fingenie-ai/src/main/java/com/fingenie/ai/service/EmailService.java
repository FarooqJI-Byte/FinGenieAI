package com.fingenie.ai.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String toEmail, String messageText) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("FinGenie Bank OTP Verification");
        message.setText(messageText);

        mailSender.send(message);
    }
    public void sendFraudAlert(String toEmail, Double amount) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("🚨 Suspicious Transaction Alert");

        message.setText(
            "Dear Customer,\n\n" +
            "A suspicious transaction was detected on your account.\n\n" +
            "Amount: ₹" + amount + "\n\n" +
            "If this was not you, please contact support immediately.\n\n" +
            "Regards,\nFinGenie Bank"
        );

        mailSender.send(message);
    }
}