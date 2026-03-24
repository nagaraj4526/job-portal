package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendApplicationEmail(String toEmail, String companyName) {
    	
    	System.out.println("company Name:"+companyName);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Application Confirmation 🚀");
        message.setText(
                "Hello,\n\n" +
                "You have successfully applied for " + companyName + ".\n\n" +
                "We Reconnected soon!!\n\n"+
                "Best of luck! 💼✨\n\n" +
                "Regards,\nJob Portal Team"
        );

        mailSender.send(message);
    }
}