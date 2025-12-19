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

}
