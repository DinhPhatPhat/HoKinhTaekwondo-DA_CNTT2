package com.hokinhtaekwondo.hokinh_taekwondo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

// Email Service
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtpEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Password Reset OTP");
            helper.setText(buildEmailContent(otp), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildEmailContent(String otp) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<h2 style='color: #333;'>Password Reset Request</h2>" +
                "<p>You have requested to reset your password.</p>" +
                "<div style='background-color: #f4f4f4; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0;'>" +
                "<p style='margin: 0; font-size: 14px; color: #666;'>Your OTP code is:</p>" +
                "<h1 style='margin: 10px 0; color: #667eea; letter-spacing: 5px;'>" + otp + "</h1>" +
                "</div>" +
                "<p style='color: #666;'>This OTP will expire in <strong>10 minutes</strong>.</p>" +
                "<p style='color: #666;'>If you didn't request this password reset, please ignore this email.</p>" +
                "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>" +
                "<p style='font-size: 12px; color: #999;'>This is an automated message, please do not reply.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}

// application.properties configuration
/*
# Email Configuration (Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000

# Application name (optional)
spring.application.name=YourAppName
*/