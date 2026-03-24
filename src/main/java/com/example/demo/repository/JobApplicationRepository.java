package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.JobApplication;
import com.example.demo.models.User;

public interface JobApplicationRepository extends JpaRepository <JobApplication,Integer>{
	List<JobApplication> findByUser(User user);

}
