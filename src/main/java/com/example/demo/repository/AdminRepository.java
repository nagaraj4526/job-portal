package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Admin;

public interface AdminRepository extends JpaRepository<Admin,Integer>{
	
	Admin findByEmail(String email);

    Admin findByEmailAndPassword(String email, String password);


}
