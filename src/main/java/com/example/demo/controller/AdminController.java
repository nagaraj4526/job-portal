package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.example.demo.models.Admin;
import com.example.demo.models.Company;
import com.example.demo.models.JobApplication;
import com.example.demo.repository.AdminRepository;

import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.JobApplicationRepository;

import jakarta.servlet.http.HttpSession;
@Controller



public class AdminController {
	
	@Autowired 
	private AdminRepository adRepo;
	@Autowired
	private JobApplicationRepository jobApplicationRepo;
	@Autowired
	private JavaMailSender mailSender;
	 private final CompanyRepository companyRepo;

	    public AdminController(CompanyRepository companyRepo) {
	        this.companyRepo = companyRepo;
	    }
	    // ─────────────────────────────────────────────────────────────
	    // INDEX
	    // ─────────────────────────────────────────────────────────────
	    @GetMapping("/")
	    public String indexpage() {
	        return "index";
	    }

	    // ─────────────────────────────────────────────────────────────
	    // ADMIN LOGIN — GET
	    // ─────────────────────────────────────────────────────────────
	    @GetMapping("/adminlogin")
	    public String adminlogin() {
	        return "adminlogin";
	    }

	    // ─────────────────────────────────────────────────────────────
	    // ADMIN LOGIN — POST
	    // ─────────────────────────────────────────────────────────────
	    @PostMapping("/adminlogin")
	    public String adminlogin(@RequestParam String email,
	                             @RequestParam String password,
	                             HttpSession session,
	                             Model model) {

	        Admin ad = adRepo.findByEmailAndPassword(email, password);

	        if (ad != null) {
	            session.setAttribute("loggedAdmin", ad);
	            session.setAttribute("role",        ad.getRole());
	            session.setAttribute("adminName",   ad.getFullName());
	            session.setAttribute("adminEmail",  ad.getEmail());
	            return "redirect:/admin/company";
	        } else {
	            model.addAttribute("error", "Invalid email or password");
	            return "adminlogin";
	        }
	    }

	    // ─────────────────────────────────────────────────────────────
	    // ADMIN REGISTRATION — GET
	    //   Renders adreg.html
	    //   Passes empty Admin object so th:object="${admin}" works
	    // ─────────────────────────────────────────────────────────────
	    @GetMapping("/adreg")
	    public String adreg(Model model) {
	        model.addAttribute("admin", new Admin());
	        return "adreg";
	    }

	    // ─────────────────────────────────────────────────────────────
	    // ADMIN REGISTRATION — POST
	    //   Triggered by: th:action="@{/adreg}" method="post"
	    //
	    //   HTML name=""              Entity field        Bound by
	    //   ──────────────────────    ─────────────────   ───────────────────────────
	    //   name="fullName"       →   Admin.fullName      @ModelAttribute (auto)
	    //   name="email"          →   Admin.email         @ModelAttribute (auto)
	    //   name="phone"          →   Admin.phone         @ModelAttribute (auto)
	    //   name="role"           →   Admin.role          @ModelAttribute (auto)
	    //   name="password"       →   Admin.password      @ModelAttribute (auto)
	    //   name="secretKey"      →   Admin.secretKey     @ModelAttribute (auto)
	    //   name="twoFactorEnabled"   boolean checkbox    @RequestParam  (defaultValue="false"
	    //                                                  because unchecked sends nothing)
	    //   name="otpCode"            hidden input        @RequestParam  (defaultValue=""
	    //                                                  filled by JS when OTP is entered)
	    // ─────────────────────────────────────────────────────────────
	    @PostMapping("/adreg")
	    public String adreg(
	            @ModelAttribute("admin") Admin admin,
	            Model model) {

	        // 1. Duplicate email check
	        if (adRepo.findByEmail(admin.getEmail()) != null) {
	            model.addAttribute("admin", admin);
	            model.addAttribute("error", "An account with this email already exists.");
	            return "adreg";
	        }

	        // 2. Validate Admin Secret Key
	        final String VALID_SECRET = "JOBPORTAL_ADMIN_2025";

	        if (admin.getSecretKey() == null || 
	            !admin.getSecretKey().trim().equals(VALID_SECRET)) {

	            model.addAttribute("admin", admin);
	            model.addAttribute("error", "Invalid admin secret key. Contact your Super Admin.");
	            return "adreg";
	        }

	        // ❌ Removed 2FA logic completely

	        // 3. Set default values
	        admin.setEmailVerified(false); // No OTP → not verified (or set true if you want)
	        admin.setStatus("ACTIVE");

	        // 4. Role fallback
	        if (admin.getRole() == null || admin.getRole().isBlank()) {
	            admin.setRole("ADMIN");
	        }

	        // 5. Save
	        adRepo.save(admin);

	        model.addAttribute("success", "Admin account created successfully! Please sign in.");
	        return "adminlogin";
	    }	    // ─────────────────────────────────────────────────────────────
	    // SEND REGISTRATION OTP
	    //   Triggered by fetch("/adreg/send-otp", { method:"POST", ... })
	    //   inside adreg.html sendOTP() function.
	    //
	    //   Frontend sends:  Content-Type: application/x-www-form-urlencoded
	    //                    body: "email=admin@example.com"
	    //
	    //   Frontend checks: if (res.ok) → show success
	    //                    else        → show error
	    //
	    //   Returns HTTP 200 OK on success, 400 Bad Request on invalid email.
	    // ─────────────────────────────────────────────────────────────
		    // ─────────────────────────────────────────────────────────────
	    // FORGOT PASSWORD — SEND OTP  (from adminlogin.html)
	    // ─────────────────────────────────────────────────────────────
	 

	    // ─────────────────────────────────────────────────────────────
	    // FORGOT PASSWORD — RESET  (from adminlogin.html)
	    // ─────────────────────────────────────────────────────────────
	    @PostMapping("/admin/forgot-password")
	    public String forgotPassword1(@RequestParam String email, Model model) {

	        Admin admin = adRepo.findByEmail(email.trim());

	        if (admin == null) {
	            model.addAttribute("error", "No admin account found for that email.");
	            return "adminlogin";
	        }

	        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

	        admin.setOtpCode(otp);
	        admin.setOtpExpiresAt(LocalDateTime.now().plusMinutes(5));
	        adRepo.save(admin);

	        // SEND EMAIL
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(email);
	        message.setSubject("Password Reset OTP");
	        message.setText("Your OTP is: " + otp);

	        mailSender.send(message);

	        model.addAttribute("otpSent", true);
	        model.addAttribute("otpEmail", email);

	        return "adminlogin";
	    }
	    @PostMapping("/admin/verify-otp")
	    public String verifyOtp(
	            @RequestParam String email,
	            @RequestParam("otpCode") String otp,
	            Model model) {

	        Admin admin = adRepo.findByEmail(email.trim());

	        if (admin == null) {
	            model.addAttribute("error", "Admin not found");
	            return "adminlogin";
	        }

	        if (admin.getOtpCode() == null) {
	            model.addAttribute("error", "Please request OTP first");
	            return "adminlogin";
	        }

	        boolean otpMatch = admin.getOtpCode().trim().equals(otp.trim());
	        boolean notExpired = LocalDateTime.now().isBefore(admin.getOtpExpiresAt());

	        if (!otpMatch) {
	            model.addAttribute("error", "Invalid OTP");
	            return "adminlogin";
	        }

	        if (!notExpired) {
	            model.addAttribute("error", "OTP Expired");
	            return "adminlogin";
	        }

	        model.addAttribute("resetEmail", email);

	        return "resetpassword";
	    }
	    @PostMapping("/admin/reset-password")
	    public String resetPassword(@RequestParam String email,
	                                @RequestParam String newPassword,
	                                Model model) {

	        Admin admin = adRepo.findByEmail(email);

	        admin.setPassword(newPassword);
	        admin.setOtpCode(null);
	        admin.setOtpExpiresAt(null);

	        adRepo.save(admin);

	        model.addAttribute("success","Password reset successful");

	        return "adminlogin";
	    }

@GetMapping("/table")
public String showTable(Model model) {
    model.addAttribute("companyList", companyRepo.findAll());
    return "table";
}
@GetMapping("/admin/delete/{id}")
public String deleteCompany(@PathVariable int id) {
    companyRepo.deleteById(id);
    return "redirect:/table";
}
@GetMapping("/admin/edit/{id}")
public String editCompany(@PathVariable int id, Model model) {
    model.addAttribute("company", companyRepo.findById(id).get());
    return "edit";
}

@PostMapping("/admin/update/{id}")
public String updateCompany(@PathVariable int id,
                            @ModelAttribute("company") Company company) {

    company.setId(id);   // ensure correct id
    companyRepo.save(company);

    return "redirect:/table";
}
@GetMapping("/company")
public String companyPage(Model model) {

    model.addAttribute("company", new Company());
    model.addAttribute("companyList", companyRepo.findAll());

    return "company";
}
// ✅ 1. View all applications
@GetMapping("/adminApplications")
public String viewApplications(Model model) {
    List<JobApplication> list = jobApplicationRepo.findAll();
    model.addAttribute("applications", list);
    return "adminApplications";
}


@PostMapping("/admin/call-interview/{id}")
public String callForInterview(@PathVariable("id") int id) {

    JobApplication app = jobApplicationRepo.findById(id).orElse(null);

    if (app != null) {
        // ✅ Update status
        app.setStatus("INTERVIEW");

        // ✅ Save in DB
        jobApplicationRepo.save(app);

        // ✅ SEND EMAIL
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(app.getEmail());
        message.setSubject("Interview Selection");

        message.setText(
            "Dear " + app.getName() + ",\n\n" +
            "Congratulations! You are selected for the interview.\n" +
            "We will contact you soon with further details.\n\n" +
            "Best Regards,\nAdmin Team"
        );

        mailSender.send(message);
    }

    return "redirect:/adminApplications";
}

@PostMapping("/admin/accept/{id}")
public String acceptApplication(@PathVariable int id) {

    JobApplication app = jobApplicationRepo.findById(id).orElse(null);

    if (app != null) {
        app.setStatus("ACCEPTED");
        jobApplicationRepo.save(app);

        // EMAIL
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(app.getEmail());
        message.setSubject("Job Application Accepted");

        message.setText(
            "Dear " + app.getName() + ",\n\n" +
            "Congratulations! You have been SELECTED for the job.\n\n" +
            "We will contact you soon.\n\n" +
            "Best Regards,\nAdmin Team"
        );

        mailSender.send(message);
    }

    return "redirect:/adminApplications";
}
@PostMapping("/admin/reject/{id}")
public String rejectApplication(@PathVariable int id) {

    JobApplication app = jobApplicationRepo.findById(id).orElse(null);

    if (app != null) {
        app.setStatus("REJECTED");
        jobApplicationRepo.save(app);

        // EMAIL
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(app.getEmail());
        message.setSubject("Job Application Update");

        message.setText(
            "Dear " + app.getName() + ",\n\n" +
            "Thank you for applying. We regret to inform you that you are not selected.\n\n" +
            "We wish you all the best for your future.\n\n" +
            "Best Regards,\nAdmin Team"
        );

        mailSender.send(message);
    }

    return "redirect:/adminApplications";
}
@PostMapping("/admin/delete-application/{id}")
public String deleteApplication(@PathVariable int id) {

    JobApplication app = jobApplicationRepo.findById(id).orElse(null);

    if (app != null) {
        if (!"ACCEPTED".equals(app.getStatus())) {
            jobApplicationRepo.delete(app);
        }
    }
    if (app != null) {
        if (!"INTERVIEW".equals(app.getStatus())) {
            jobApplicationRepo.delete(app);
        }
    }

    return "redirect:/adminApplications";
}
}






