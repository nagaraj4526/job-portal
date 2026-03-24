package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Company;


public interface JobRepository extends JpaRepository<Company, Integer> {

    List<Company> findByCompanyNameContainingIgnoreCase(String keyword);

}