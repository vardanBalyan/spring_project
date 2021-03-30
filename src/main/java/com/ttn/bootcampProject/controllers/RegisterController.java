package com.ttn.bootcampProject.controllers;

import com.ttn.bootcampProject.entities.Customer;
import com.ttn.bootcampProject.services.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegisterController {

    @Autowired
    RegisterService service;

    @PostMapping(path = "/register-customer")
    public ResponseEntity<String> registerCustomer(@RequestBody Customer customer)
    {
        service.registerCustomer(customer);
        return new ResponseEntity("Customer registered successfully!!",HttpStatus.CREATED);
    }

    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> confirmUserAccount(@RequestParam("token")String confirmationToken) {
        return service.confirmCustomer(confirmationToken);
    }
}
