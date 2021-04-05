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
import org.springframework.stereotype.Repository;

@Repository
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
        customer.addAddress(registerCustomer.getAddress());

        // setting the role for the customer
        Role role = roleRepository.findByAuthority("ROLE_CUSTOMER");
        UserRole roleOfCustomer = new UserRole(customer, role);
        customer.addRoles(roleOfCustomer);

        customerRepository.save(customer);

        // creating new confirmation token
        ConfirmationToken confirmationToken = new ConfirmationToken(customer);
        confirmationTokenRepository.save(confirmationToken);

        // sending the mail to the user to activate account
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(customer.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("vardanbalyan97@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                +"http://localhost:8080/confirm-account?token="
                +confirmationToken.getConfirmationToken());

        mailService.sendRegisterMail(mailMessage);
        return new ResponseEntity("Customer registered successfully!!. " +
                "Please check your mail to activate your account.",HttpStatus.CREATED);
    }

    public ResponseEntity<String> confirmCustomer(String confirmationToken)
    {
        // getting the token from the database
        ConfirmationToken token = confirmationTokenRepository
                .findByConfirmationToken(confirmationToken);

        // checking if token exist
        if (token != null) {
            //  getting user from the userid associated with the confirmation token
            User user = userRepository.findByEmail(token.getUser().getEmail());
            // activating user account
            user.setActive(true);
            userRepository.save(user);
            return new ResponseEntity("Customer activated successfully!!", HttpStatus.CREATED);
        }
        return new ResponseEntity("Incorrect confirmation token.!",HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> resendActivationLink(String email)
    {
        User user = userRepository.findByEmail(email);
        //
        ConfirmationToken token = confirmationTokenRepository.findByUserId(user.getId());

        // sending mail
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("vardanbalyan97@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                +"http://localhost:8080/confirm-account?token="
                +token.getConfirmationToken());

        mailService.sendRegisterMail(mailMessage);
        return new ResponseEntity("Please check your mail to activate your account."
                ,HttpStatus.CREATED);
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
        seller.addAddress(registerSeller.getAddress());

        // setting role for the seller
        Role role = roleRepository.findByAuthority("ROLE_SELLER");
        UserRole roleOfSeller = new UserRole(seller, role);
        seller.addRoles(roleOfSeller);

        sellerRepository.save(seller);

        // sending mail for account creation
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(seller.getEmail());
        mailMessage.setSubject("Account created successfully!!");
        mailMessage.setFrom("vardanbalyan97@gmail.com");
        mailMessage.setText("Seller account successfully created and waiting for the approval.");

        mailService.sendRegisterMail(mailMessage);

        return new ResponseEntity("Seller register successfully!!",HttpStatus.CREATED);
    }
}
