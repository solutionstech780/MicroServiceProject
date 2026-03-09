package com.customer_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Async   // ✅ Added for parallel email sending
    public void sendEmail(String toEmail, String name, Long id) throws MessagingException, InterruptedException {
        System.out.println(
                "Started: " + Thread.currentThread().getName()
        );

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("Welcome to Bank");

        String htmlContent = buildEmailTemplate(name, id);

        helper.setText(htmlContent, true); // true = HTML email
        Thread.sleep(5000);
        mailSender.send(message);

        // ✅ Optional: to verify parallel threads
        System.out.println(
                "Completed: " + Thread.currentThread().getName()
        );
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