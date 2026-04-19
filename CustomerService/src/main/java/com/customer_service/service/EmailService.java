package com.customer_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username:}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    public void sendEmail(String toEmail, String name, Long id) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        if (fromEmail != null && !fromEmail.isBlank()) {
            helper.setFrom(fromEmail);
        }
        helper.setTo(toEmail);
        helper.setSubject("Welcome to Bank");

        String htmlContent = buildEmailTemplate(name, id);

        helper.setText(htmlContent, true); // true = HTML email
        mailSender.send(message);

        log.info("Welcome email sent to {} (customerId={})", toEmail, id);
    }

    private String buildEmailTemplate(String name, Long id) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; background-color:#f4f6f8; padding:20px;">
                
                    <div style="max-width:600px; margin:auto; background:white; padding:30px; border-radius:8px;">

                        <h2 style="color:#00395d;"> Bank</h2>
                        <hr/>

                        <p>Dear <b>%s</b>,</p>

                        <p>
                            Welcome to <b> Bank</b>
                            Your customer account has been successfully created.
                        </p>

                        <div style="background:#f1f1f1; padding:15px; border-radius:5px;">
                            <p><b>Customer ID:</b> %d</p>
                        </div>

                        <p>
                            Please keep this ID confidential and do not share it with anyone.
                        </p>

                        <br/>
                        <p>Regards,<br/>
                        Customer Support Team</p>

                        <hr/>
                        <p style="font-size:12px; color:gray;">
                            This is an automated email. Please do not reply.
                            © 2026 Bank. All rights reserved.
                        </p>

                    </div>
                </body>
                </html>
                """.formatted(name, id);
    }
}