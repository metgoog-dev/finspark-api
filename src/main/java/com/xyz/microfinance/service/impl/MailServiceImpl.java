package com.xyz.microfinance.service.impl;

import com.xyz.microfinance.service.MailService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from:noreply@microfinance.local}")
    private String fromAddress;

    @Value("${spring.mail.host:}")
    private String smtpHost;

    @Value("${spring.mail.port:}")
    private String smtpPort;

    @Value("${spring.mail.username:}")
    private String smtpUser;

    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtpEmail(String toEmail, String username, String otp) {
        try {
            log.info("Attempting to send OTP email to: {} via SMTP host: {} port: {}", toEmail, smtpHost, smtpPort);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String effectiveFrom = (fromAddress == null || fromAddress.isBlank()) ? smtpUser : fromAddress;
            helper.setFrom(effectiveFrom);
            helper.setTo(toEmail);
            helper.setSubject("Your Microfinance OTP");
            helper.setText(buildOtpHtml(username, otp), true);
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email via SMTP host={} port={} user={} - cause: {}", smtpHost, smtpPort, redact(smtpUser), e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private String buildOtpHtml(String username, String otp) {
        return "" +
                "<div style='font-family:Arial,sans-serif;max-width:600px;margin:auto;padding:16px;border:1px solid #eee;border-radius:8px'>" +
                "  <h2 style='color:#0a6'>Microfinance Email Verification</h2>" +
                "  <p>Hi " + escape(username) + ",</p>" +
                "  <p>Your one-time password (OTP) is:</p>" +
                "  <div style='font-size:28px;font-weight:700;letter-spacing:3px;background:#f7f7f7;padding:12px 16px;border-radius:6px;display:inline-block'>" + escape(otp) + "</div>" +
                "  <p style='margin-top:16px'>This code expires in 10 minutes. If you didn't request this, you can ignore this email.</p>" +
                "  <hr style='border:none;border-top:1px solid #eee;margin:16px 0'/>" +
                "  <p style='color:#777;font-size:12px'>Microfinance API</p>" +
                "</div>";
    }

    private String escape(String input) {
        return input == null ? "" : input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    private String redact(String value) {
        if (value == null || value.isBlank()) return "";
        int at = value.indexOf('@');
        if (at > 2) {
            return value.substring(0, 2) + "***" + value.substring(at);
        }
        return "***";
    }
}

