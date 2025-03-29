package ru.miit.libe.services;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Service("emailService")
public class EmailService {
    private final JavaMailSender javaMailSender;
    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendEmail(String from, String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // true = multipart
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = HTML

        // отправление на почту только если включена функция
        if (Objects.requireNonNull(Dotenv.load().get("MAIL_SERVICE")).equals("1")) {
            javaMailSender.send(message);
        }
        else {
            System.out.println("\t\t*send message to "+to+"*\n\t\tcontent:\n\t\t"+htmlContent);
        }
    }
}
