package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.models.Admin;
import com.example.demo.models.Company;
import com.example.demo.repository.CompanyRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/company")
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepo;

    // ✅ Show Add Form
    @GetMapping
    public String showAddForm(Model model, HttpSession session) {

        if (session.getAttribute("loggedAdmin") == null) {
            return "redirect:/adminlogin";
        }

        model.addAttribute("company", new Company());  // 🔥 VERY IMPORTANT
        return "company";   // your html file name
    }

    // ✅ Save Company
    @PostMapping
    public String saveCompany(@ModelAttribute Company company,
                              HttpSession session,
                              RedirectAttributes redirectAttributes
             
                              ) {

    	 Admin admin = (Admin) session.getAttribute("loggedAdmin");  // 🔥 ADD THIS

    	    if (admin == null)  {
            return "redirect:/adminlogin";
        }
        company.setAdmin(admin);

        companyRepo.save(company);
        redirectAttributes.addFlashAttribute("success",
                "Added Successfully!");
        return "redirect:/admin/company";
    }
      }

