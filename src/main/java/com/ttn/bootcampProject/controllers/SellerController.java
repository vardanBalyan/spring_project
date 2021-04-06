package com.ttn.bootcampProject.controllers;

import com.ttn.bootcampProject.dtos.AddProductDto;
import com.ttn.bootcampProject.dtos.SellerProfileDto;
import com.ttn.bootcampProject.dtos.UpdatePasswordDto;
import com.ttn.bootcampProject.dtos.DisplayCategoryDto;
import com.ttn.bootcampProject.entities.Address;
import com.ttn.bootcampProject.services.CategoryService;
import com.ttn.bootcampProject.services.ProductService;
import com.ttn.bootcampProject.services.SellerDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class SellerController {

    @Autowired
    SellerDaoService sellerDaoService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;

    @GetMapping("/seller/profile")
    public SellerProfileDto customerProfile(Principal principal) {
        return sellerDaoService.getProfile(principal.getName());
    }

    @PatchMapping("/seller/update-profile")
    public ResponseEntity<String> updateCustomerProfile(@Valid @RequestBody SellerProfileDto sellerProfileDto
            , Principal principal)
    {
        return sellerDaoService.updateProfile(sellerProfileDto, principal.getName());
    }

    @PatchMapping("/seller/update-password")
    public ResponseEntity<String> updateCustomerPassword(@Valid @RequestBody UpdatePasswordDto passwordDto
            , Principal principal)
    {
        return sellerDaoService.updatePassword(passwordDto, principal.getName());
    }

    @PatchMapping("/seller/update-address/{id}")
    public ResponseEntity<String> updateAddress(@Valid @RequestBody Address address
            , @PathVariable long id, Principal principal)
    {
        return sellerDaoService.updateAnAddress(address, id, principal.getName());
    }

    @GetMapping("/seller/category")
    public List<DisplayCategoryDto> viewAllCategory()
    {
        return categoryService.viewAllCategoryForSeller();
    }

    @PostMapping("/seller/add-product")
    public ResponseEntity<String> addAProduct(@RequestBody AddProductDto addProductDto, Principal principal)
    {
        return productService.addAProduct(addProductDto, principal.getName());
    }
}
