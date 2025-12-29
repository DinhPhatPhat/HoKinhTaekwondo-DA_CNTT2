package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.registration.RegistrationRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class RegistrationService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${registration.admin.email:admin@example.com}")
    private String adminEmail;

    public void processRegistration(RegistrationRequest request) {
        // Send confirmation email to user only if email is provided
        if (request.hasEmail()) {
            sendConfirmationEmailToUser(request);
        }

        // Always send notification email to admin
        sendNotificationEmailToAdmin(request);
    }

    private void sendConfirmationEmailToUser(RegistrationRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(request.getEmail());
            helper.setSubject("Xác Nhận Đăng Ký - Hồ Kính Taekwondo");
            helper.setText(buildUserConfirmationEmail(request), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send confirmation email to user", e);
        }
    }

    private void sendNotificationEmailToAdmin(RegistrationRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(adminEmail);
            helper.setSubject("Đăng Ký Mới - " + request.getName());
            helper.setText(buildAdminNotificationEmail(request), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send notification email to admin", e);
        }
    }

    private String buildUserConfirmationEmail(RegistrationRequest request) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +

                "<div style='background: linear-gradient(to right, #dc2626, #2563eb); padding: 30px; border-radius: 10px 10px 0 0; text-align: center;'>" +
                "<h1 style='color: white; margin: 0;'>Hồ Kính Taekwondo</h1>" +
                "</div>" +

                "<div style='background-color: #f9fafb; padding: 30px; border-radius: 0 0 10px 10px;'>" +
                "<h2 style='color: #1f2937;'>Xin chào " + request.getName() + "!</h2>" +
                "<p style='color: #4b5563; font-size: 16px;'>Cảm ơn bạn đã đăng ký tham gia Hồ Kính Taekwondo.</p>" +
                "<p style='color: #4b5563; font-size: 16px;'>Chúng tôi đã nhận được thông tin đăng ký của bạn và sẽ liên hệ lại trong thời gian sớm nhất.</p>" +

                "<div style='background-color: white; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #2563eb;'>" +
                "<h3 style='color: #1f2937; margin-top: 0;'>Thông tin đăng ký:</h3>" +
                "<p style='margin: 5px 0; color: #4b5563;'><strong>Họ tên:</strong> " + request.getName() + "</p>" +
                (request.hasEmail() ?
                        "<p style='margin: 5px 0; color: #4b5563;'><strong>Email:</strong> " + request.getEmail() + "</p>" : "") +
                "<p style='margin: 5px 0; color: #4b5563;'><strong>Số điện thoại:</strong> " + request.getPhoneNumber() + "</p>" +
                (request.getNote() != null && !request.getNote().isEmpty() ?
                        "<p style='margin: 5px 0; color: #4b5563;'><strong>Ghi chú:</strong> " + request.getNote() + "</p>" : "") +
                "</div>" +

                "<p style='color: #4b5563; font-size: 14px;'>Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi.</p>" +
                "</div>" +

                "<div style='text-align: center; padding: 20px; color: #9ca3af; font-size: 12px;'>" +
                "<p>Email này được gửi tự động, vui lòng không trả lời.</p>" +
                "<p>&copy; 2024 Hồ Kính Taekwondo. All rights reserved.</p>" +
                "</div>" +

                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildAdminNotificationEmail(RegistrationRequest request) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        return "<html>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +

                "<div style='background: linear-gradient(to right, #dc2626, #2563eb); padding: 30px; border-radius: 10px 10px 0 0; text-align: center;'>" +
                "<h1 style='color: white; margin: 0;'>Đăng Ký Mới</h1>" +
                "</div>" +

                "<div style='background-color: #f9fafb; padding: 30px; border-radius: 0 0 10px 10px;'>" +
                "<p style='color: #4b5563; font-size: 16px;'>Có một đăng ký mới từ website!</p>" +
                "<p style='color: #6b7280; font-size: 14px;'>Thời gian: " + now.format(formatter) + "</p>" +

                "<div style='background-color: white; padding: 20px; border-radius: 8px; margin: 20px 0;'>" +
                "<h3 style='color: #1f2937; margin-top: 0; border-bottom: 2px solid #e5e7eb; padding-bottom: 10px;'>Thông tin người đăng ký:</h3>" +
                "<table style='width: 100%; border-collapse: collapse;'>" +
                "<tr>" +
                "<td style='padding: 10px; color: #6b7280; font-weight: bold; width: 140px;'>Họ tên:</td>" +
                "<td style='padding: 10px; color: #1f2937;'>" + request.getName() + "</td>" +
                "</tr>" +
                (request.hasEmail() ?
                        "<tr style='background-color: #f9fafb;'>" +
                                "<td style='padding: 10px; color: #6b7280; font-weight: bold;'>Email:</td>" +
                                "<td style='padding: 10px; color: #1f2937;'>" + request.getEmail() + "</td>" +
                                "</tr>" :
                        "<tr style='background-color: #fef3c7;'>" +
                                "<td style='padding: 10px; color: #6b7280; font-weight: bold;'>Email:</td>" +
                                "<td style='padding: 10px; color: #92400e;'><em>Không cung cấp</em></td>" +
                                "</tr>") +
                "<tr" + (request.hasEmail() ? "" : " style='background-color: #f9fafb;'") + ">" +
                "<td style='padding: 10px; color: #6b7280; font-weight: bold;'>Số điện thoại:</td>" +
                "<td style='padding: 10px; color: #1f2937;'>" + request.getPhoneNumber() + "</td>" +
                "</tr>" +
                (request.getNote() != null && !request.getNote().isEmpty() ?
                        "<tr style='background-color: " + (request.hasEmail() ? "#f9fafb" : "") + ";'>" +
                                "<td style='padding: 10px; color: #6b7280; font-weight: bold; vertical-align: top;'>Ghi chú:</td>" +
                                "<td style='padding: 10px; color: #1f2937;'>" + request.getNote() + "</td>" +
                                "</tr>" : "") +
                "</table>" +
                "</div>" +

                "<div style='background-color: #fef3c7; border-left: 4px solid #f59e0b; padding: 15px; border-radius: 4px;'>" +
                "<p style='margin: 0; color: #92400e; font-size: 14px;'><strong>⚠️ Lưu ý:</strong> Vui lòng liên hệ lại với người đăng ký qua số điện thoại trong thời gian sớm nhất.</p>" +
                "</div>" +

                "</div>" +

                "</div>" +
                "</body>" +
                "</html>";
    }
}