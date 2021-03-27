package com.ttn.bootcampProject.resources;

import com.ttn.bootcampProject.helpingclasses.CustomerInfo;
import com.ttn.bootcampProject.services.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminResource {

    @Autowired
    UserDao userService;

    @GetMapping(path = "/admin/customers")
    public List<CustomerInfo> listAllCustomers()
    {
        return userService.getAllCustomers();
    }
}
