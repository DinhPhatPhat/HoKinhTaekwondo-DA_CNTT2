package com.hokinhtaekwondo.hokinh_taekwondo.controller;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.otp.ApiResponse;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.otp.OtpRequest;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.otp.ResetPasswordRequest;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.otp.VerifyOtpRequest;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.ChangeFirstPasswordRequest;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.service.AuthService;
import com.hokinhtaekwondo.hokinh_taekwondo.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    @Autowired
    private OtpService otpService;

    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAll(@AuthenticationPrincipal User user) {

        try {
            authService.logoutAll(user);
            return ResponseEntity.ok("Đăng xuất thành công ở tất cả thiết bị");
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Request OTP via email
    @PostMapping("/forgot-password/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody OtpRequest request) {
        try {
            otpService.generateAndSendOtp(request.getEmail());
            return ResponseEntity.ok(new ApiResponse(true, "Mã OTP đã được chuyển đến email của bạn"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // Verify OTP
    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtp());

        if (isValid) {
            return ResponseEntity.ok(new ApiResponse(true, "Xác thực OTP thành công"));
        } else {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Mã OTP không hợp lệ hoặc đã hết hạn"));
        }
    }

    // Reset Password
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            otpService.resetPassword(request.getEmail(), request.getNewPassword());
            return ResponseEntity.ok(new ApiResponse(true, "Cập nhật mật khẩu thành công. Hãy đăng nhập với mật khẩu mới"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Cập nhật mật khẩu thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/change-first-password")
    public ResponseEntity<?> changeFirstPassword(@AuthenticationPrincipal User user,
                                           @RequestBody ChangeFirstPasswordRequest request) {
        try {
            authService.changeFirstPassword(user, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("Cập nhật mật khẩu thành công. Bạn sẽ cần đăng nhập lại tài khoản để có thể tiếp tục sử dụng hệ thống");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Cập nhật mật khẩu thất bại: " + e.getMessage());
        }
    }
}
