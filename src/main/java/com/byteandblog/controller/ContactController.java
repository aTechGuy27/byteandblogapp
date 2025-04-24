package com.byteandblog.controller;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byteandblog.entity.ContactMessage;
import com.byteandblog.repository.ContactMessageRepository;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String email;

    @PostMapping
    public ResponseEntity<ContactMessage> createMessage(@Valid @RequestBody ContactMessage message) {
        logger.info("Creating new contact message from: {}", message.getEmail());
        message.setCreatedAt(LocalDateTime.now());
        this.sendSelfMessage(message);
        this.sendUserMessage(message);
        ContactMessage savedMessage = contactMessageRepository.save(message);
        logger.info("Contact message created successfully with ID: {}", savedMessage.getId());
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping
    public Page<ContactMessage> getMessages(Pageable pageable) {
        logger.info("Fetching contact messages, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<ContactMessage> messages = contactMessageRepository.findAll(pageable);
        logger.debug("Retrieved {} contact messages", messages.getTotalElements());
        return messages;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactMessage> getMessageByID(@PathVariable Long id) {
        logger.info("Fetching contact message with ID: {}", id);
        return contactMessageRepository.findById(id)
                .map(message -> {
                    logger.debug("Contact message found from: {}", message.getEmail());
                    return ResponseEntity.ok(message);
                })
                .orElseGet(() -> {
                    logger.warn("Contact message with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    private void sendSelfMessage(ContactMessage message) {
        try {
            SimpleMailMessage smtpmessage = new SimpleMailMessage();
            smtpmessage.setTo(email);
            smtpmessage.setSubject(message.getName() + " Sent you a Message!! ->");
            smtpmessage.setText(message.getMessage() + "\n" + message.getEmail());
            logger.info("Sending self notification email to: {}", email);
            mailSender.send(smtpmessage);
            logger.info("Self notification email sent successfully to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send self notification email to {}: {}", email, e.getMessage(), e);
        }
    }

    private void sendUserMessage(ContactMessage message) {
        try {
            SimpleMailMessage smtpmessage = new SimpleMailMessage();
            smtpmessage.setTo(message.getEmail());
            smtpmessage.setSubject("Hi " + message.getName() + "!!!");
            smtpmessage.setText("Thanks for Your Message, we will get back to you soon!!!");
            logger.info("Sending confirmation email to user: {}", message.getEmail());
            mailSender.send(smtpmessage);
            logger.info("Confirmation email sent successfully to user: {}", message.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send confirmation email to user {}: {}", message.getEmail(), e.getMessage(), e);
        }
    }
}