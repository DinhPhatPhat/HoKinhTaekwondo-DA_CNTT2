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
                "<h2 style='color: #333;'>Yêu Cầu Đặt Lại Mật Khẩu</h2>" +
                "<p>Bạn đã yêu cầu đặt lại mật khẩu của mình.</p>" +
                "<div style='background-color: #f4f4f4; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0;'>" +
                "<p style='margin: 0; font-size: 14px; color: #666;'>Mã OTP của bạn là:</p>" +
                "<h1 style='margin: 10px 0; color: #667eea; letter-spacing: 5px;'>" + otp + "</h1>" +
                "</div>" +
                "<p style='color: #666;'>Mã OTP này sẽ hết hạn trong <strong>10 phút</strong>.</p>" +
                "<p style='color: #666;'>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>" +
                "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>" +
                "<p style='font-size: 12px; color: #999;'>Đây là email tự động, vui lòng không trả lời.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}