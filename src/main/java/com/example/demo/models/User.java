package com.example.demo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;         // username / login name
    private String password;
    private String role;         // "CANDIDATE" or "EMPLOYER"

    @Column(name = "full_name")
    private String fullName;

    private String email;
    private String phone;

    // OTP fields for forgot-password flow
    @Column(name = "reset_otp")
    private String resetOtp;

    @Column(name = "otp_expiry")
    private java.time.LocalDateTime otpExpiry;

    // ── Getters & Setters ──────────────────────────

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getResetOtp() {
        return resetOtp;
    }
    public void setResetOtp(String resetOtp) {
        this.resetOtp = resetOtp;
    }

    public java.time.LocalDateTime getOtpExpiry() {
        return otpExpiry;
    }
    public void setOtpExpiry(java.time.LocalDateTime otpExpiry) {
        this.otpExpiry = otpExpiry;
    }
}