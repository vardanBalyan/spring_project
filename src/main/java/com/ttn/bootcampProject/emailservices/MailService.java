package com.ttn.bootcampProject.emailservices;

import com.ttn.bootcampProject.entities.Customer;
import com.ttn.bootcampProject.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void sendUserActivationMail(User user) throws MailException
    {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(user.getEmail());
        mail.setFrom("vardanbalyan97@gmail.com");
        mail.setSubject("Activation mail");
        mail.setText("Your account now has been activated.");

        javaMailSender.send(mail);
    }

    @Async
    public void sendRegisterActivationMail(SimpleMailMessage mail)
    {
        javaMailSender.send(mail);
    }

    @Async
    public void sendForgotPasswordMail(SimpleMailMessage mail)
    {
        javaMailSender.send(mail);
    }
}
