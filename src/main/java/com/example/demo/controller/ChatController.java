package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ChatController {

    @PostMapping("/chat")
    public String chat(@RequestBody String message) {

        message = message.toLowerCase().trim();

        // ── Greetings ──
        if (message.contains("hello") || message.contains("hi") || message.contains("hey")) {
            return "Hello! 👋 Welcome to JobPortal. How can I help you today?";
        }

        if (message.contains("good morning")) {
            return "Good morning! ☀️ Ready to find your dream job today?";
        }

        if (message.contains("good evening") || message.contains("good afternoon")) {
            return "Good evening! 🌙 How can I assist you?";
        }

        // ── Job Search ──
        if (message.contains("search job") || message.contains("find job") || message.contains("looking for job")) {
            return "🔍 You can search jobs using the search bar on the home page. Try searching by job title, company name, or location!";
        }

        if (message.contains("job") && message.contains("chennai")) {
            return "📍 We have multiple job openings in Chennai! Use the search bar and type 'Chennai' to see all available positions.";
        }

        if (message.contains("job") && message.contains("bangalore")) {
            return "📍 Bangalore has tons of opportunities! Search 'Bangalore' in the job search to explore them.";
        }

        if (message.contains("job") && message.contains("remote")) {
            return "🏠 We have remote job listings too! Search 'Remote' in the job search bar to find work-from-home opportunities.";
        }

        if (message.contains("job")) {
            return "💼 You can search and apply for jobs directly from our home page. Use the search bar to filter by title, company, or location!";
        }

        // ── Apply ──
        if (message.contains("apply") || message.contains("application")) {
            return "📝 To apply for a job: \n1. Login to your account\n2. Search for a job\n3. Click 'Apply Now'\n4. Fill in your details and submit!";
        }

        if (message.contains("application status") || message.contains("my application")) {
            return "📋 You can check your application status by going to your Dashboard → My Applications. It shows Applied, Reviewed, and Shortlisted statuses.";
        }

        // ── Account / Login / Register ──
        if (message.contains("register") || message.contains("sign up") || message.contains("create account")) {
            return "✅ Creating an account is easy! Click 'User Login' → 'Register', fill in your name, email, and password. It's free!";
        }

        if (message.contains("login") || message.contains("sign in")) {
            return "🔐 Click the 'User Login' button at the top right corner of the page to sign in to your account.";
        }

        if (message.contains("forgot password") || message.contains("reset password")) {
            return "🔑 Click 'Forgot Password' on the login page and enter your registered email. We'll send you a reset link shortly.";
        }

        if (message.contains("logout") || message.contains("sign out")) {
            return "👋 You can log out by clicking your profile icon at the top right and selecting 'Logout'.";
        }

        // ── Profile ──
        if (message.contains("profile") || message.contains("resume") || message.contains("cv")) {
            return "📄 You can update your profile, upload your resume, and add your skills under Dashboard → My Profile. A complete profile gets more visibility!";
        }

        if (message.contains("upload resume") || message.contains("attach resume")) {
            return "📎 Go to Dashboard → My Profile → Upload Resume. We accept PDF and DOCX formats up to 5MB.";
        }

        // ── Hiring / Companies / Post Jobs ──
        if (message.contains("post job") || message.contains("hiring") || message.contains("recruit")) {
            return "🏢 Want to hire? Login as Admin → Post a Job → fill in the job title, description, location, and salary. Your listing goes live instantly!";
        }

        if (message.contains("company") || message.contains("companies")) {
            return "🏬 We have 500+ registered companies on our portal spanning IT, Finance, Healthcare, and more. Browse them in the Jobs section!";
        }

        // ── Salary ──
        if (message.contains("salary") || message.contains("pay") || message.contains("ctc")) {
            return "💰 Each job listing shows the salary or CTC range. You can filter jobs by salary in the advanced search options.";
        }

        // ── Skills / Freshers / Experience ──
        if (message.contains("fresher") || message.contains("entry level") || message.contains("no experience")) {
            return "🎓 Great news! We have many fresher-friendly jobs. Search 'Fresher' or '0-1 years' in the search bar to find entry-level opportunities.";
        }

        if (message.contains("experience") || message.contains("senior") || message.contains("experienced")) {
            return "💼 We have roles for all experience levels — Junior, Mid-level, and Senior. Use filters to narrow down jobs matching your experience.";
        }

        if (message.contains("skill") || message.contains("technology") || message.contains("tech stack")) {
            return "🛠️ You can add your skills to your profile to get matched with relevant jobs. Popular skills on our portal include Java, Python, React, and SQL.";
        }

        // ── IT / Software Jobs ──
        if (message.contains("software") || message.contains("developer") || message.contains("engineer")) {
            return "💻 We have hundreds of software and engineering jobs! Try searching 'Software Engineer', 'Java Developer', or 'React Developer' in the search bar.";
        }

        if (message.contains("java")) {
            return "☕ Java is one of the most in-demand skills on our portal! Search 'Java Developer' to see current openings across Chennai, Bangalore, and Remote.";
        }

        if (message.contains("python")) {
            return "🐍 Python roles are booming! Search 'Python Developer' or 'Data Scientist' to find Python-related jobs on the portal.";
        }

        if (message.contains("data") && (message.contains("analyst") || message.contains("science") || message.contains("engineer"))) {
            return "📊 Data roles are highly popular! Search 'Data Analyst', 'Data Scientist', or 'Data Engineer' to explore opportunities.";
        }

        // ── Notifications / Alerts ──
        if (message.contains("notification") || message.contains("alert") || message.contains("email alert")) {
            return "🔔 Enable job alerts in your Dashboard settings to get notified by email when new jobs matching your profile are posted!";
        }

        // ── Contact / Support ──
        if (message.contains("contact") || message.contains("support") || message.contains("help")) {
            return "📞 Need help? You can reach us at:\n📧 support@jobportal.com\n📱 +91 98765 43210\nOr use the Contact form on this page!";
        }

        if (message.contains("address") || message.contains("office") || message.contains("location")) {
            return "📍 Our office is located in Chennai, Tamil Nadu, India. Office hours: Mon–Fri 9AM–6PM, Saturday 10AM–2PM.";
        }

        // ── Farewell ──
        if (message.contains("bye") || message.contains("goodbye") || message.contains("see you") || message.contains("thank")) {
            return "👋 Thank you for visiting JobPortal! Wishing you all the best in your job search. See you soon! 😊";
        }

        if (message.contains("ok") || message.contains("okay") || message.contains("got it") || message.contains("thanks")) {
            return "😊 Great! Feel free to ask me anything else about jobs, applications, or your account.";
        }

        // ── About the Portal ──
        if (message.contains("about") || message.contains("what is this") || message.contains("portal")) {
            return "🌐 JobPortal is a platform connecting job seekers with top companies across India. We have 1000+ active jobs, 500+ companies, and 50,000+ successful hires!";
        }

        // ── Default fallback ──
        return "🤔 I'm not sure about that. You can ask me about:\n• Searching jobs\n• Applying for jobs\n• Creating an account\n• Posting a job\n• Salary or skills\nOr contact support at support@jobportal.com 😊";
    }
}