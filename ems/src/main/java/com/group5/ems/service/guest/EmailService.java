package com.group5.ems.service.guest;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendContactEmail(
            String name,
            String email,
            String phone,
            String topic,
            String message) {

        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo("trantrungd83@gmail.com"); // email nhận
        mail.setSubject("New Contact Message: " + topic);

        String body =
                "Name: " + name + "\n" +
                        "Email: " + email + "\n" +
                        "Phone: " + phone + "\n\n" +
                        "Message:\n" + message;

        mail.setText(body);

        mailSender.send(mail);
    }
}