package com.ttn.bootcampProject.controllers;

import com.ttn.bootcampProject.dtos.RegisterCustomerDto;
import com.ttn.bootcampProject.dtos.RegisterSellerDto;
import com.ttn.bootcampProject.services.RegisterUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class RegisterUserController {

    @Autowired
    RegisterUserService service;

    @PostMapping(path = "/register-customer")
    public ResponseEntity<String> registerCustomer(@Valid @RequestBody RegisterCustomerDto customerDto)
    {
        return service.registerCustomer(customerDto);
    }

    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> confirmUserAccount(@RequestParam("token")String confirmationToken) {
        return service.confirmCustomer(confirmationToken);
    }

    @PostMapping(path = "/register-seller")
    public ResponseEntity<String> registerSeller(@Valid @RequestBody RegisterSellerDto sellerDto)
    {
        return service.registerSeller(sellerDto);
    }

    @PostMapping(path = "/resend-activation-mail/{email}")
    public ResponseEntity<String> resendActivationMail(@PathVariable String email)
    {
        return service.resendActivationLink(email);
    }
}
