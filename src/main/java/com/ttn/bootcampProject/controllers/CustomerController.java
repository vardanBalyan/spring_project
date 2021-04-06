package com.ttn.bootcampProject.controllers;

import com.ttn.bootcampProject.dtos.CustomerProfileDto;
import com.ttn.bootcampProject.dtos.UpdatePasswordDto;
import com.ttn.bootcampProject.dtos.ViewAllCategoryForCustomerDto;
import com.ttn.bootcampProject.entities.Address;
import com.ttn.bootcampProject.services.CategoryService;
import com.ttn.bootcampProject.services.CustomerDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class CustomerController {

    @Autowired
    CustomerDaoService customerDaoService;
    @Autowired
    CategoryService categoryService;

    @GetMapping("/customer/profile")
    public CustomerProfileDto customerProfile(Principal principal) {
        return customerDaoService.getProfile(principal.getName());
    }

    @GetMapping("/customer/address")
    public List<Address> customerAddresses(Principal principal) {
        return customerDaoService.getAddresses(principal.getName());
    }

    @PatchMapping("/customer/update-profile")
    public ResponseEntity<String> updateCustomerProfile(@Valid @RequestBody CustomerProfileDto customerProfileDto
            , Principal principal)
    {
        return customerDaoService.updateProfile(customerProfileDto, principal.getName());
    }

    @PatchMapping("/customer/update-password")
    public ResponseEntity<String> updateCustomerPassword(@Valid @RequestBody UpdatePasswordDto passwordDto
            , Principal principal)
    {
        return customerDaoService.updatePassword(passwordDto, principal.getName());
    }

    @PatchMapping("/customer/add-address")
    public ResponseEntity<String> addNewAddress(@Valid @RequestBody Address address
            , Principal principal)
    {
        return customerDaoService.addNewAddress(address, principal.getName());
    }

    @PatchMapping("/customer/update-address/{id}")
    public ResponseEntity<String> updateAddress(@Valid @RequestBody Address address
            , @PathVariable long id, Principal principal)
    {
        return customerDaoService.updateAnAddress(address, id, principal.getName());
    }

    @DeleteMapping("/customer/delete-address/{id}")
    public ResponseEntity<String> deleteAnAddress(@PathVariable long id, Principal principal)
    {
        return customerDaoService.deleteAnAddress(id, principal.getName());
    }

    @GetMapping("/customer/category/{id}")
    public List<ViewAllCategoryForCustomerDto> viewAllCategory(@PathVariable Long id)
    {
        return categoryService.viewCategoryForCustomer(id);
    }

}
