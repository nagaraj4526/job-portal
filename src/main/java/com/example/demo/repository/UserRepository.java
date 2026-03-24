package com.example.demo.repository;

import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Used by login — matches name + password directly (plain text, no encoder)
    User findByNameAndPassword(String name, String password);

    // Used by register — duplicate checks
    boolean existsByName(String name);
    boolean existsByEmail(String email);

    // Used by forgot-password OTP flow
    Optional<User> findByEmail(String email);
}