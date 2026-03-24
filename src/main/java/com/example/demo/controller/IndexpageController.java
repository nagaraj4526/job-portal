package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.models.Contact;
import com.example.demo.repository.ContactRepository;

@Controller
public class IndexpageController {
	
	@Autowired
    private JavaMailSender mailSender;
	  @Autowired
	    private ContactRepository contactRepository;

	
	
	@GetMapping("home")
	public String home() {
		return "index";
	}
	

    @PostMapping("/contactSave")
    public String saveContact(@ModelAttribute Contact contact) {

        // save to database
        contactRepository.save(contact);

        // send email
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo("yourmail@gmail.com");
        mail.setSubject("New Contact Message");
        
        mail.setText(
                "Name : " + contact.getName() +
                "\nEmail : " + contact.getEmail() +
                "\nMessage : " + contact.getMessage()
        );

        mailSender.send(mail);

        return "redirect:/contact?success";
	

    }
    }
