package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    @Transactional
    public void logoutAll(User user) {
        // Update new login pin in DB
        int newVersion = user.getLoginPin() + 1;
        user.setLoginPin(newVersion);
        userRepository.save(user);
    }

    @Transactional
    public void changeFirstPassword(User user, String oldPassword, String newPassword) {
        if(user.getRole() == 0 || user.getRole() == 1) {
            throw new IllegalArgumentException("Lỗi không hợp lệ");
        }
        // Update new login pin in DB
        if(user.getLoginPin() != 0) {
            throw new IllegalArgumentException("Không thể thay đổi mật khẩu lần đầu");
        }
        if(!oldPassword.equals(user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ đã nhập sai");
        }
        if(oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }
        user.setPassword(newPassword);
        int newVersion = 1;
        user.setLoginPin(newVersion);
        userRepository.save(user);
    }
}
