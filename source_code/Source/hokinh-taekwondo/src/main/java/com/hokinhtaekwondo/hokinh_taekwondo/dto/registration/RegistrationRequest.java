package com.hokinhtaekwondo.hokinh_taekwondo.dto.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RegistrationRequest {

    @NotBlank(message = "Tên là bắt buộc")
    private String name;

    @Email(message = "Email không hợp lệ")
    private String email = "";

    @NotBlank(message = "Số điện thoại là bắt buộc")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải có 10 đến 11 chữ số")
    private String phoneNumber;

    private String note;

    // Constructors
    public RegistrationRequest() {}

    public RegistrationRequest(String name, String email, String phoneNumber, String note) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.note = note;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // Helper method to check if email is provided
    public boolean hasEmail() {
        return email != null && !email.trim().isEmpty();
    }
}