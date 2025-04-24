package com.byteandblog.service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();
    private final Random random = new Random();

    // Generate a 6-digit OTP
    public String generateOtp(String email) {
        String otp = String.format("%06d", random.nextInt(999999));
        otpStore.put(email, new OtpEntry(otp, System.currentTimeMillis()));
        return otp;
    }

    // Validate OTP
    public boolean validateOtp(String email, String otp) {
        OtpEntry entry = otpStore.get(email);
        if (entry == null) {
            return false;
        }
        // Check if OTP is valid and not expired (5 minutes)
        long currentTime = System.currentTimeMillis();
        if (currentTime - entry.getTimestamp() > 5 * 60 * 1000) { // 5 minutes
            otpStore.remove(email);
            return false;
        }
        boolean isValid = entry.getOtp().equals(otp);
        if (isValid) {
            otpStore.remove(email); // Remove OTP after successful validation
        }
        return isValid;
    }

    static class OtpEntry {
        private final String otp;
        private final long timestamp;

        public OtpEntry(String otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }

        public String getOtp() {
            return otp;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}