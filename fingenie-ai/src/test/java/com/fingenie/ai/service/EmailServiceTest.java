package com.fingenie.ai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    @BeforeEach
    void setup() {
        messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    }

    // ✅ Test sendEmail()
    @Test
    void sendEmail_shouldSendCorrectMessage() {

        String email = "test@gmail.com";
        String messageText = "Your OTP is 123456";

        emailService.sendEmail(email, messageText);

        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals(email, sentMessage.getTo()[0]);
        assertEquals("FinGenie Bank OTP Verification", sentMessage.getSubject());
        assertEquals(messageText, sentMessage.getText());
    }

    // ✅ Test sendFraudAlert()
    @Test
    void sendFraudAlert_shouldSendFraudMessage() {

        String email = "user@gmail.com";
        Double amount = 5000.0;

        emailService.sendFraudAlert(email, amount);

        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals(email, sentMessage.getTo()[0]);
        assertEquals("🚨 Suspicious Transaction Alert", sentMessage.getSubject());

        assertTrue(sentMessage.getText().contains("₹5000.0"));
        assertTrue(sentMessage.getText().contains("suspicious transaction"));
    }
}