package com.ttn.bootcampProject.resources;

import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.helpingclasses.CustomerInfo;
import com.ttn.bootcampProject.helpingclasses.SellersInfo;
import com.ttn.bootcampProject.services.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin")
public class AdminResource {

    @Autowired
    UserDao userService;

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
       return userService.activateSeller(id);
    }

    @PatchMapping(path = "/seller/deactivate/{id}")
    public String deactivateSeller(@PathVariable long id)
    {
        return userService.deactivateSeller(id);
    }

    @PatchMapping(path = "/customer/activate/{id}")
    public String activateCustomer(@PathVariable long id)
    {
        return userService.activateCustomer(id);
    }

    @PatchMapping(path = "/customer/deactivate/{id}")
    public String deactivateCustomer(@PathVariable long id)
    {
        return userService.deactivateCustomer(id);
    }
}
