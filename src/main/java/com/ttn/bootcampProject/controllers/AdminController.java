package com.ttn.bootcampProject.controllers;

import com.ttn.bootcampProject.emailservices.ActivationMailService;
import com.ttn.bootcampProject.entities.Customer;
import com.ttn.bootcampProject.entities.Seller;
import com.ttn.bootcampProject.helpingclasses.CustomerInfo;
import com.ttn.bootcampProject.helpingclasses.SellersInfo;
import com.ttn.bootcampProject.services.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    ActivationMailService mailService;

    @GetMapping(path = "/customers")
    public List<CustomerInfo> listAllCustomers()
    {
        return userService.getAllCustomers();
    }

    @GetMapping(path = "/sellers")
    public List<SellersInfo> listAllSellers()
    {
        return userService.getAllSellers();
    }

    @PatchMapping(path = "/seller/activate/{id}")
    public String activateSeller(@PathVariable long id)
    {
        Seller seller = userService.activateSeller(id);

        if(seller!=null)
        {
            return seller.getEmail();
        }

        return "error occured";
    }

    @PatchMapping(path = "/seller/deactivate/{id}")
    public String deactivateSeller(@PathVariable long id)
    {
        return userService.deactivateSeller(id);
    }

    @PatchMapping(path = "/customer/activate/{id}")
    public String activateCustomer(@PathVariable long id)
    {
        Customer customer =userService.activateCustomer(id);

        if(customer != null)
        {
            try {
                mailService.sendCustomerActivationMail(customer);
            }catch (MailException e)
            {
                //error
                logger.info("error occurred"+ e.getMessage());
            }
            return "Customer is active";
        }
        else
            return "customer does not exist";
    }

    @PatchMapping(path = "/customer/deactivate/{id}")
    public String deactivateCustomer(@PathVariable long id)
    {
        return userService.deactivateCustomer(id);
    }
}
