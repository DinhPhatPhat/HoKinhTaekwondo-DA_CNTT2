package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.model.OtpVerification;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.OtpVerificationRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class OtpService {

    @Autowired
    private OtpVerificationRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_VALIDITY_MINUTES = 10;

    // Generate and send OTP via email
    public void generateAndSendOtp(String email) {
        // Check if user exists
        System.out.println(email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email này"));

        // Delete any existing OTP for this email
        otpRepository.deleteByEmail(email);

        // Generate OTP
        String otp = generateOtp();

        // Save to database
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setEmail(email);
        otpVerification.setOtp(passwordEncoder.encode(otp));
        otpVerification.setExpiryTime(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
        otpVerification.setVerified(false);
        otpRepository.save(otpVerification);

        // Send OTP via email
        emailService.sendOtpEmail(email, otp);
    }

    // Verify OTP
    public boolean verifyOtp(String email, String otp) {

        Optional<OtpVerification> otpVerificationOpt =
                otpRepository.findByEmailAndVerifiedFalse(email);

        if (otpVerificationOpt.isEmpty()) {
            return false;
        }

        OtpVerification otpVerification = otpVerificationOpt.get();

        // Check expiry
        if (LocalDateTime.now().isAfter(otpVerification.getExpiryTime())) {
            return false;
        }

        // Check OTP using matches()
        if (!passwordEncoder.matches(otp, otpVerification.getOtp())) {
            return false;
        }

        // Mark as verified
        otpVerification.setVerified(true);
        otpRepository.save(otpVerification);

        return true;
    }


    // Reset password
    public void resetPassword(String email, String newPassword) {

        OtpVerification otpVerification = otpRepository
                .findByEmailAndVerifiedTrue(email)
                .orElseThrow(() -> new RuntimeException("Không xác thực được OTP"));

        if (LocalDateTime.now().isAfter(otpVerification.getExpiryTime())) {
            throw new RuntimeException("OTP đã hết hạn");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpRepository.deleteByEmail(email);
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

}
