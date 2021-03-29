package com.ttn.bootcampProject.emailservices;

import com.ttn.bootcampProject.entities.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ActivationMailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendCustomerActivationMail(Customer customer) throws MailException
    {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(customer.getEmail());
        mail.setFrom("vardan.balyan.vb@gmail.com");
        mail.setSubject("Activation mail");
        mail.setText("Your account now has been activated.");

        javaMailSender.send(mail);
    }
}
