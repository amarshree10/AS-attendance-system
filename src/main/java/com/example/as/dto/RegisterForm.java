package com.example.as.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class RegisterForm {

    @NotBlank(message = "Username is required.")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Letters, numbers, and underscores only.")
    private String username;

    @NotBlank(message = "Password is required.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9!@#$%^&*()_+=\\-{}\\[\\]:;'<>,.?/\\\\|`~]{8,}$",
            message = "Must be at least 8 characters and include letters and numbers.")
    private String password;

    @NotBlank(message = "ConfirmPassword is required.")
    private String confirmPassword;

    @NotBlank(message = "First Name is required.")
    private String firstname;

    @NotBlank(message = "Last Name is required.")
    private String lastname;

    @Column(nullable = false,unique = true)
    private String email;

    @NotBlank(message = "Phone No is required.")
    @Pattern(regexp = "^0\\d{9,10}$", message = "Must be 10–11 digits.")
    private String phone;

    @NotNull(message = "Date of Birth is required.")
    private LocalDate birthday;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
}
