package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Company;
import com.example.demo.repository.JobRepository;

@RestController
@RequestMapping("/jobs")
public class JobRestController {

    private final JobRepository jobRepository;

    public JobRestController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @GetMapping("/search")
    public List<Company> searchJobs(@RequestParam("keyword") String keyword) {
        return jobRepository
                .findByCompanyNameContainingIgnoreCase(keyword);
    }
}