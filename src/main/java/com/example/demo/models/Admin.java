package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // ── Matches name="fullName" in adreg form ──────────────────
    @Column(nullable = false)
    private String fullName;

    // ── Matches name="email" ───────────────────────────────────
    @Column(nullable = false, unique = true)
    private String email;

    // ── Matches name="phone" ───────────────────────────────────
    @Column(nullable = false)
    private String phone;

    // ── Matches name="password" ────────────────────────────────
    @Column(nullable = false)
    private String password;

    // ── Matches name="role" (select dropdown) ──────────────────
    // Values: super_admin | hr_manager | recruiter |
    //         content_mod | analytics  | support
    @Column(nullable = false)
    private String role;

    // ── Matches name="secretKey" ───────────────────────────────
    @Column(nullable = false)
    @JsonIgnore
    private String secretKey;

   
    // ── OTP storage for 2FA and forgot-password flow ───────────
    @JsonIgnore
    private String otpCode;

    private LocalDateTime otpExpiresAt;

    // ── Marked true after OTP is verified ──────────────────────
    @Column(nullable = false)
    private boolean emailVerified = false;

    // ── Account status: ACTIVE | INACTIVE | SUSPENDED ──────────
    @Column(nullable = false)
    private String status = "ACTIVE";

    // ── Audit timestamps (auto-managed) ────────────────────────
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // ── Relationship ────────────────────────────────────────────
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Company> companies;

    // ── Lifecycle hooks ─────────────────────────────────────────
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Getters & Setters ────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }

   

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }

    public LocalDateTime getOtpExpiresAt() { return otpExpiresAt; }
    public void setOtpExpiresAt(LocalDateTime otpExpiresAt) { this.otpExpiresAt = otpExpiresAt; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Company> getCompanies() { return companies; }
    public void setCompanies(List<Company> companies) { this.companies = companies; }
}