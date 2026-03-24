package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Company;

public interface CompanyRepository extends JpaRepository<Company,Integer> {

}
