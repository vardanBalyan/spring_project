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
    public void sendActivationMail(SimpleMailMessage mail) throws MailException
    {
        javaMailSender.send(mail);
    }

    @Async
    public void sendRegisterMail (SimpleMailMessage mail) throws MailException
    {
        javaMailSender.send(mail);
    }

    @Async
    public void sendForgotPasswordMail(SimpleMailMessage mail) throws MailException
    {
        javaMailSender.send(mail);
    }
}
