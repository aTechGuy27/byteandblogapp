package com.byteandblog.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byteandblog.entity.User;
import com.byteandblog.repository.UserRepository;
import com.byteandblog.service.OtpService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private OtpService otpService;

	@Value("${jwt.secret}")
	private String jwtSecret;

	@SuppressWarnings("serial")
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody User user) {
		logger.info("Register request for username: {}", user.getUsername());
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			logger.warn("Registration failed: Username {} already exists", user.getUsername());
			return ResponseEntity.badRequest().body("Username already exists");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRoles(new HashSet<>() {
			{
				add("ROLE_USER");
			}
		});
		userRepository.save(user);
		logger.info("User {} registered successfully", user.getUsername());
		return ResponseEntity.ok("User registered successfully");
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody User loginUser) {
		logger.info("Login request for username: {}", loginUser.getUsername());
		User user = userRepository.findByUsername(loginUser.getUsername()).orElseThrow(() -> {
			logger.error("Login failed: User {} not found", loginUser.getUsername());
			return new RuntimeException("User not found");
		});
		if (passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
			String token = Jwts.builder().setSubject(user.getUsername()).claim("roles", user.getRoles()) // Include
																											// roles in
																											// the token
					.setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + 86400000))
					.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
			logger.info("User {} logged in successfully", user.getUsername());
			return ResponseEntity.ok(new JwtResponse(token, user.getId()));
		}
		logger.warn("Login failed: Invalid credentials for user {}", loginUser.getUsername());
		return ResponseEntity.status(401).body("Invalid credentials");
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
		logger.info("Forgot password request for email: {}", request.getEmail());
		Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
		if (!optionalUser.isPresent()) {
			logger.warn("Forgot password failed: Email {} not found", request.getEmail());
			return ResponseEntity.badRequest().body(new MessageResponse("error", "Email not found"));
		}
		String otp = otpService.generateOtp(request.getEmail());
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(request.getEmail());
			message.setSubject("Password Reset OTP");
			message.setText("Your OTP for password reset is: " + otp + "\nThis OTP is valid for 5 minutes.");
			logger.info("Sending OTP email to: {}", request.getEmail());
			mailSender.send(message);
			logger.info("OTP email sent successfully to: {}", request.getEmail());
			return ResponseEntity.ok(new MessageResponse("success", "OTP sent to your email"));
		} catch (Exception e) {
			logger.error("Failed to send OTP email to {}: {}", request.getEmail(), e.getMessage(), e);
			return ResponseEntity.status(500)
					.body(new MessageResponse("error", "Failed to send OTP: " + e.getMessage()));
		}
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
		logger.info("Verify OTP request for email: {}", request.getEmail());
		boolean isValid = otpService.validateOtp(request.getEmail(), request.getOtp());
		if (isValid) {
			logger.info("OTP verified successfully for email: {}", request.getEmail());
			return ResponseEntity.ok(new MessageResponse("success", "OTP verified successfully"));
		}
		logger.warn("Verify OTP failed: Invalid or expired OTP for email: {}", request.getEmail());
		return ResponseEntity.badRequest().body(new MessageResponse("error", "Invalid or expired OTP"));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
		logger.info("Reset password request for email: {}", request.getEmail());
		Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
		if (!optionalUser.isPresent()) {
			logger.warn("Reset password failed: Email {} not found", request.getEmail());
			return ResponseEntity.badRequest().body(new MessageResponse("error", "Email not found"));
		}
		User user = optionalUser.get();
		if (!request.getPassword().equals(request.getConfirmPassword())) {
			logger.warn("Reset password failed: Passwords do not match for email: {}", request.getEmail());
			return ResponseEntity.badRequest().body(new MessageResponse("error", "Passwords do not match"));
		}
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		userRepository.save(user);
		logger.info("Password reset successfully for email: {}", request.getEmail());
		return ResponseEntity.ok(new MessageResponse("success", "Password reset successfully"));
	}

	static class JwtResponse {
		private final String token;
		private final Long userId;

		JwtResponse(String token, Long userId) {
			this.token = token;
			this.userId = userId;
		}

		public String getToken() {
			return token;
		}

		public Long getUserId() {
			return userId;
		}
	}

	static class MessageResponse {
		private final String status;
		private final String message;

		MessageResponse(String status, String message) {
			this.status = status;
			this.message = message;
		}

		public String getStatus() {
			return status;
		}

		public String getMessage() {
			return message;
		}
	}

	static class ForgotPasswordRequest {
		private String email;

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
	}

	static class VerifyOtpRequest {
		private String email;
		private String otp;

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getOtp() {
			return otp;
		}

		public void setOtp(String otp) {
			this.otp = otp;
		}
	}

	static class ResetPasswordRequest {
		private String email;
		private String password;
		private String confirmPassword;

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getConfirmPassword() {
			return confirmPassword;
		}

		public void setConfirmPassword(String confirmPassword) {
			this.confirmPassword = confirmPassword;
		}
	}
}