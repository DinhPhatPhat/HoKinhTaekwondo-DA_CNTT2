package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.registration.RegistrationRequest;
import com.hokinhtaekwondo.hokinh_taekwondo.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendRegistration(
            @Valid @RequestBody RegistrationRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {
            registrationService.processRegistration(request);

            response.put("success", true);
            response.put("message", "Registration email sent successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send registration: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
