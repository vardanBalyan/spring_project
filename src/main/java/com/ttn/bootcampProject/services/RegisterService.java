package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.emailservices.ActivationMailService;
import com.ttn.bootcampProject.entities.ConfirmationToken;
import com.ttn.bootcampProject.entities.Customer;
import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.repos.ConfirmationTokenRepository;
import com.ttn.bootcampProject.repos.CustomerRepository;
import com.ttn.bootcampProject.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    ActivationMailService mailService;
    @Autowired
    CustomerRepository customerRepository;


    public void registerCustomer(Customer customer)
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        customer.setPassword(encoder.encode(customer.getPassword()));
        customerRepository.save(customer);

        ConfirmationToken confirmationToken = new ConfirmationToken(customer);
        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(customer.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("vardanbalyan97@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                +"http://localhost:8080/confirm-account?token="+confirmationToken.getConfirmationToken());

        mailService.sendRegisterActivationMail(mailMessage);
    }

    public ResponseEntity<String> confirmCustomer(String confirmationToken)
    {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userRepository.findByEmail(token.getUser().getEmail());
            user.setActive(true);
            userRepository.save(user);
            return new ResponseEntity("Customer activated successfully!!", HttpStatus.CREATED);
        }
        return null;
    }
}
