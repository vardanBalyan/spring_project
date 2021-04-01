package com.ttn.bootcampProject.controllers;

import com.ttn.bootcampProject.emailservices.MailService;
import com.ttn.bootcampProject.entities.Customer;
import com.ttn.bootcampProject.entities.Seller;
import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.dtos.CustomerInfoDto;
import com.ttn.bootcampProject.dtos.SellersInfoDto;
import com.ttn.bootcampProject.exceptions.UserNotFoundException;
import com.ttn.bootcampProject.services.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin")
public class AdminController {

    private Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    UserDao userService;
    @Autowired
    MailService mailService;

    @GetMapping(path = "/customers")
    public List<CustomerInfoDto> listAllCustomers()
    {
        return userService.getAllCustomers();
    }

    @GetMapping(path = "/sellers")
    public List<SellersInfoDto> listAllSellers()
    {
        return userService.getAllSellers();
    }

    @PatchMapping(path = "/seller/activate/{id}")
    public ResponseEntity<String> activateSeller(@PathVariable long id)
    {
        return userService.activateSeller(id);
    }

    @PatchMapping(path = "/seller/deactivate/{id}")
    public ResponseEntity<String> deactivateSeller(@PathVariable long id)
    {
       return userService.deactivateSeller(id);
    }

    @PatchMapping(path = "/customer/activate/{id}")
    public ResponseEntity<String> activateCustomer(@PathVariable long id)
    {
        return userService.activateCustomer(id);
    }

    @PatchMapping(path = "/customer/deactivate/{id}")
    public ResponseEntity<String> deactivateCustomer(@PathVariable long id)
    {
        return userService.deactivateCustomer(id);
    }

    @GetMapping(path = "/all-info/user")
    public List<User> allInfoOfUsers()
    {
        return userService.giveAllUsers();
    }
}
