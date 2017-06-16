package com.itransition.pmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

/**
 * Created by Lenovo on 14.06.2017.
 */
@Service
public class EmailService {
    @Autowired
    private JavaMailSenderImpl mailSender;

    public void sendEmail(String to,String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@promanager.com");
        message.setTo(to);
        message.setSubject("Activate your account!");
        message.setText("Link for activation: " + link);
        mailSender.send(message);
    }
}
