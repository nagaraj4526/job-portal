package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.models.Company;
import com.example.demo.models.JobApplication;
import com.example.demo.models.User;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.JobApplicationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Controller
public class UserController {

    @Autowired private EmailService         emailService;
    @Autowired private UserRepository       userRepo;
    @Autowired private CompanyRepository    companyRepo;
    @Autowired private JobApplicationRepository jobApplicationRepo;
    @Autowired private JavaMailSender       mailSender;

    // ══════════════════════════════════════════════════════════
    //  LOGIN
    // ══════════════════════════════════════════════════════════

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error",  required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error  != null) model.addAttribute("error",   "Invalid username or password. Please try again.");
        if (logout != null) model.addAttribute("success", "You have been logged out successfully.");
        return "login";
    }

    
        
    @PostMapping("/login")
    public String loginSubmit(@RequestParam String name,
                             @RequestParam String password,
                             HttpSession session,
                             Model model) {

        User user = userRepo.findByNameAndPassword(name, password);

        if (user != null) {

            session.setAttribute("loggedUser", user);
            session.setAttribute("role", user.getRole());
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());

            // ✅ SMS send
           
            return "redirect:/user/jobs";
        }

        model.addAttribute("error", "Invalid username or password.");
        return "login";
    }
    // ══════════════════════════════════════════════════════════
    //  REGISTER
    //  Fields from your register.html form:
    //    th:field="*{fullName}"  → User.fullName
    //    th:field="*{name}"      → User.name      (username)
    //    th:field="*{email}"     → User.email
    //    th:field="*{phone}"     → User.phone
    //    th:field="*{password}"  → User.password
    //    th:field="*{role}"      → User.role      (CANDIDATE / EMPLOYER)
    //    name="confirmPassword"  → @RequestParam  (not an entity field)
    // ══════════════════════════════════════════════════════════

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute("user") User user,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Model model) {

        // Duplicate username check
        if (userRepo.existsByName(user.getName())) {
            model.addAttribute("error", "Username '" + user.getName() + "' is already taken.");
            return "register";
        }
        if (user.getPhone() == null || user.getPhone().length() != 10) {
            model.addAttribute("error", "Enter valid 10-digit mobile number");
            return "register";
        }
        

        // Duplicate email check
        if (userRepo.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "An account with this email already exists.");
            return "register";
        }

        // Password match check
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "register";
        }

        // Password length check
        if (user.getPassword().length() < 8) {
            model.addAttribute("error", "Password must be at least 8 characters.");
            return "register";
        }

        // Default role guard (radio button should always send it, but just in case)
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("CANDIDATE");
        }

        // Save — plain text password, no encoder (matches your project style)
        userRepo.save(user);

        model.addAttribute("success", "Account created successfully! You can now sign in.");
        model.addAttribute("user", new User());   // reset form
        return "register";
    }

    // ══════════════════════════════════════════════════════════
    //  LOGOUT
    // ══════════════════════════════════════════════════════════

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }

    // ══════════════════════════════════════════════════════════
    //  FORGOT PASSWORD — Step 1: Show page
    // ══════════════════════════════════════════════════════════

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(
            @RequestParam(value = "step", defaultValue = "1") int step,
            HttpSession session,
            Model model) {

        model.addAttribute("step", step);
        model.addAttribute("resetEmail", session.getAttribute("resetEmail"));

        return "forgot-password";
    }
    // ══════════════════════════════════════════════════════════
    //  FORGOT PASSWORD — Step 2: Send OTP to email
    // ══════════════════════════════════════════════════════════

    @PostMapping("/forgot-password/send-otp")
    public String sendOtp(@RequestParam("email") String email,
                          HttpSession session,
                          RedirectAttributes ra,
                          Model model) {
    	
        Optional<User> optUser = userRepo.findByEmail(email);

        if (optUser.isEmpty()) {
            // Generic message — don't reveal whether email is registered
            ra.addFlashAttribute("success", "If that email is registered, a code has been sent.");
            return "redirect:/forgot-password?step=1";
        }

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(1_000_000));

        User user = optUser.get();
        user.setResetOtp(otp);                                         // plain text — short-lived
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepo.save(user);

        session.setAttribute("resetEmail", email);

        try {
            sendOtpEmail(email, user.getFullName(), otp);
        } catch (MessagingException ex) {
            ra.addFlashAttribute("error", "Failed to send email. Please try again.");
            return "redirect:/forgot-password?step=1";
        }
        model.addAttribute("step", 2);   // IMPORTANT
        model.addAttribute("resetEmail", email);


        ra.addFlashAttribute("success", "A 6-digit code was sent to " + email);
        return "redirect:/forgot-password?step=2";
    }

    // ══════════════════════════════════════════════════════════
    //  FORGOT PASSWORD — Step 3: Verify OTP
    // ══════════════════════════════════════════════════════════

    @PostMapping("/forgot-password/verify-otp")
    public String verifyOtp(@RequestParam("otp") String otp,
                            HttpSession session,
                            RedirectAttributes ra) {

        String email = (String) session.getAttribute("resetEmail");

        if (email == null) {
            ra.addFlashAttribute("error", "Invalid request.");
            return "redirect:/forgot-password?step=1";
        }

        Optional<User> optUser = userRepo.findByEmail(email);

        if (optUser.isEmpty()) {
            ra.addFlashAttribute("error", "Invalid request.");
            return "redirect:/forgot-password?step=1";
        }

        User user = optUser.get();

        if (user.getOtpExpiry() == null || LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            ra.addFlashAttribute("error", "OTP expired. Please request a new one.");
            return "redirect:/forgot-password?step=1";
        }

        if (user.getResetOtp() == null || !user.getResetOtp().equals(otp.trim())) {
            ra.addFlashAttribute("error", "Incorrect code.");
            return "redirect:/forgot-password?step=2";
        }

        session.setAttribute("otpVerified", true);

        return "redirect:/forgot-password?step=3";
    }    
    // reset

    @PostMapping("/forgot-password/reset")
    public String resetPassword(@RequestParam("email")           String email,
                                @RequestParam("newPassword")     String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                HttpSession session,
                                RedirectAttributes ra) {

        Boolean verified    = (Boolean) session.getAttribute("otpVerified");
        String  sessionEmail = (String)  session.getAttribute("resetEmail");

        // Guard — must have passed OTP step
        if (verified == null || !verified || !email.equals(sessionEmail)) {
            ra.addFlashAttribute("error", "Session expired. Please start again.");
            return "redirect:/forgot-password?step=1";
        }

        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/forgot-password?step=3";
        }

        if (newPassword.length() < 8) {
            ra.addFlashAttribute("error", "Password must be at least 8 characters.");
            return "redirect:/forgot-password?step=3";
        }

        Optional<User> optUser = userRepo.findByEmail(email);
        if (optUser.isEmpty()) {
            ra.addFlashAttribute("error", "User not found.");
            return "redirect:/forgot-password?step=1";
        }

        User user = optUser.get();
        user.setPassword(newPassword);     // plain text — no encoder
        user.setResetOtp(null);
        user.setOtpExpiry(null);
        userRepo.save(user);

        // Clean session
        session.removeAttribute("resetEmail");
        session.removeAttribute("otpVerified");

        ra.addFlashAttribute("success", "Password reset successfully! Please sign in.");
        return "redirect:/login";
    }

    // ══════════════════════════════════════════════════════════
    //  JOBS
    // ══════════════════════════════════════════════════════════

    @GetMapping("/user/jobs")
    public String viewJobs(Model model, HttpSession session) {
        if (session.getAttribute("loggedUser") == null) return "redirect:/login";
        model.addAttribute("companyList", companyRepo.findAll());
        return "userJobs";
    }

    // ══════════════════════════════════════════════════════════
    //  APPLY
    // ══════════════════════════════════════════════════════════

    @GetMapping("/user/apply/{id}")
    public String showApplyForm(@PathVariable int id,
                                Model model,
                                HttpSession session) {
        if (session.getAttribute("loggedUser") == null) return "redirect:/login";
        Company company = companyRepo.findById(id).orElse(null);
        JobApplication application = new JobApplication();
        application.setCompany(company);
        model.addAttribute("application", application);
        model.addAttribute("company",     company);
        List<String> positions = List.of(
                "Java Developer",
                "Frontend Developer",
                "Backend Developer",
                "Full Stack Developer",
                "Tester"
            );

           
            model.addAttribute("positions", positions);
        return "applyForm";
    }

    @PostMapping("/user/apply")
    public String submitApplication(@ModelAttribute JobApplication application,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        application.setUser(user);

        int     companyId = application.getCompany().getId();
        Company company   = companyRepo.findById(companyId).orElse(null);
        application.setCompany(company);

        jobApplicationRepo.save(application);

        if (company != null) {
            emailService.sendApplicationEmail(
                application.getEmail(),
                company.getCompanyName()
            );
        }

        redirectAttributes.addFlashAttribute("success", "Applied Successfully! ✅");
        return "redirect:/user/dashboard";
    }

    // ══════════════════════════════════════════════════════════
    //  DASHBOARD
    // ══════════════════════════════════════════════════════════

    @GetMapping("/user/dashboard")
    public String dashboard(Model model, HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        List<JobApplication> applications = jobApplicationRepo.findByUser(user);

        long acceptedCount = applications.stream()
                .filter(a -> "ACCEPTED".equals(a.getStatus()))
                .count();

        model.addAttribute("acceptedCount", acceptedCount);
        long pendingCount = applications.stream()
                .filter(a -> a.getStatus() == null || "PENDING".equals(a.getStatus()))
                .count();

        model.addAttribute("pendingCount", pendingCount);
        long rejectedCount = applications.stream()
                .filter(a -> "REJECTED".equals(a.getStatus()))
                .count();

       

        // User fields from your entity — name, id, role, fullName, email, phone
        model.addAttribute("userName",    user.getName());
        model.addAttribute("userFullName",user.getFullName());
        model.addAttribute("userEmail",   user.getEmail());
        model.addAttribute("userPhone",   user.getPhone());
        model.addAttribute("userId",      user.getId());
        model.addAttribute("userRole",    user.getRole());
        model.addAttribute("loggedUser",  user);

        model.addAttribute("applications", applications);
        model.addAttribute("totalApps", applications.size());

        model.addAttribute("acceptedCount", acceptedCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("rejectedCount", rejectedCount);
        return "userDashboard";
    }

    // ══════════════════════════════════════════════════════════
    //  HELPER — Send OTP Email
    // ══════════════════════════════════════════════════════════

    private void sendOtpEmail(String to, String name, String otp) throws MessagingException {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("JobPortal — Your Password Reset Code: " + otp);
        helper.setFrom("noreply@jobportal.com");

        String html = """
            <div style="font-family:sans-serif;max-width:480px;margin:auto;background:#0a0a0f;
                        color:#fff;padding:40px;border-radius:16px;border:1px solid rgba(255,255,255,0.08)">
              <h2 style="font-size:24px;margin-bottom:8px">Password Reset Code</h2>
              <p style="color:rgba(255,255,255,0.5);font-size:14px;margin-bottom:32px">
                Hi %s, use the code below to reset your password.
              </p>
              <div style="background:rgba(245,200,66,0.1);border:1px solid rgba(245,200,66,0.3);
                          border-radius:12px;padding:24px;text-align:center;margin-bottom:28px">
                <span style="font-size:42px;font-weight:700;letter-spacing:12px;color:#f5c842">%s</span>
              </div>
              <p style="color:rgba(255,255,255,0.35);font-size:12px">
                This code expires in <strong style="color:rgba(255,255,255,0.6)">10 minutes</strong>.
                If you did not request this, you can safely ignore this email.
              </p>
            </div>
            """.formatted(name != null ? name : "there", otp);

        helper.setText(html, true);
        mailSender.send(msg);
    }
    @GetMapping("/user/applications")
    public String userApplications(Model model) {

        List<JobApplication> list = jobApplicationRepo.findAll(); // filter later by user

        model.addAttribute("applications", list);

        return "userApplications";
    }
}