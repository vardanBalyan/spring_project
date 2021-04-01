package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.dtos.RegisterCustomerDto;
import com.ttn.bootcampProject.dtos.RegisterSellerDto;
import com.ttn.bootcampProject.emailservices.MailService;
import com.ttn.bootcampProject.entities.*;
import com.ttn.bootcampProject.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    MailService mailService;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    SellerRepository sellerRepository;


    public ResponseEntity<String> registerCustomer(RegisterCustomerDto registerCustomer)
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Customer customer = new Customer();
        customer.setEmail(registerCustomer.getEmail());
        customer.setFirstName(registerCustomer.getFirstName());
        customer.setMiddleName(registerCustomer.getMiddleName());
        customer.setLastName(registerCustomer.getLastName());
        customer.setPassword(encoder.encode(registerCustomer.getPassword()));
        customer.setContact(registerCustomer.getContact());
        customer.setAddresses(registerCustomer.getAddresses());

        // setting the role for the customer
        Role role = roleRepository.findByAuthority("ROLE_CUSTOMER");
        UserRole roleOfCustomer = new UserRole(customer, role);
        customer.addRoles(roleOfCustomer);

        customerRepository.save(customer);

        ConfirmationToken confirmationToken = new ConfirmationToken(customer);
        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(customer.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("vardanbalyan97@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                +"http://localhost:8080/confirm-account?token="
                +confirmationToken.getConfirmationToken());

        mailService.sendRegisterActivationMail(mailMessage);
        return new ResponseEntity("Customer registered successfully!!. " +
                "Please check your mail to activate your account.",HttpStatus.CREATED);
    }

    public ResponseEntity<String> confirmCustomer(String confirmationToken)
    {
        ConfirmationToken token = confirmationTokenRepository
                .findByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userRepository.findByEmail(token.getUser().getEmail());
            user.setActive(true);
            userRepository.save(user);
            return new ResponseEntity("Customer activated successfully!!", HttpStatus.CREATED);
        }
        return new ResponseEntity("Incorrect confirmation token.!",HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> registerSeller(RegisterSellerDto registerSeller)
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Seller seller = new Seller();
        seller.setEmail(registerSeller.getEmail());
        seller.setFirstName(registerSeller.getFirstName());
        seller.setMiddleName(registerSeller.getMiddleName());
        seller.setLastName(registerSeller.getFirstName());
        seller.setPassword(encoder.encode(registerSeller.getPassword()));
        seller.setGst(registerSeller.getGst());
        seller.setCompanyName(registerSeller.getCompanyName());
        seller.setCompanyContact(registerSeller.getCompanyContact());
        seller.setAddresses(registerSeller.getAddresses());

        Role role = roleRepository.findByAuthority("ROLE_SELLER");
        UserRole roleOfSeller = new UserRole(seller, role);
        seller.addRoles(roleOfSeller);

        sellerRepository.save(seller);

        return new ResponseEntity("Seller register successfully!!",HttpStatus.CREATED);
    }
}
