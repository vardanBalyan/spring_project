package com.ttn.bootcampProject.controllers;

import com.ttn.bootcampProject.dtos.AddCategoryDto;
import com.ttn.bootcampProject.dtos.CategoryMetadataFieldValuesDto;
import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.dtos.GetAllCustomerInfoDto;
import com.ttn.bootcampProject.dtos.GetAllSellersInfoDto;
import com.ttn.bootcampProject.entities.products.categories.CategoryMetadataField;
import com.ttn.bootcampProject.exceptions.CategoryNotFoundException;
import com.ttn.bootcampProject.exceptions.UserNotFoundException;
import com.ttn.bootcampProject.services.CategoryService;
import com.ttn.bootcampProject.services.UserDaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/admin")
public class AdminController {

    private Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    UserDaoService userService;
    @Autowired
    CategoryService categoryService;

    @GetMapping(path = "/customers")
    public List<GetAllCustomerInfoDto> listAllCustomers()
    {
        return userService.getAllCustomers();
    }

    @GetMapping(path = "/sellers")
    public List<GetAllSellersInfoDto> listAllSellers()
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

    @PostMapping("/add-category")
    public ResponseEntity<String> addCategory(@Valid @RequestBody AddCategoryDto categoryDto)
    {
        return categoryService.addCategory(categoryDto);
    }

    @GetMapping("/category/{id}")
    public AddCategoryDto viewACategory(@PathVariable long id)
    {
        AddCategoryDto categoryDto = categoryService.viewACategory(id);
        if(categoryDto == null)
        {
            throw new CategoryNotFoundException("No category found for the specified category id.");
        }
        return categoryDto;
    }

    @GetMapping("/category")
    public List<AddCategoryDto> viewAllCategory()
    {
        return categoryService.viewAllCategoryAdmin();
    }

    @PatchMapping("/update-category")
    public ResponseEntity<String> updateCategory(@Valid @RequestBody AddCategoryDto categoryDto)
    {
        return categoryService.updateCategory(categoryDto);
    }

    @PostMapping("/add-category-metadata-field")
    public ResponseEntity<String> addCategoryMetadataField(@RequestParam("fieldName") @NotNull String fieldName)
    {
        return categoryService.addMetadataField(fieldName);
    }

    @GetMapping("/category-metadata-field")
    public List<CategoryMetadataField> getAllMetadataFields()
    {
        return categoryService.viewAllMetadataFields();
    }

    @PostMapping("/add-metadata-field-values")
    public ResponseEntity<String> addMetadataFieldValues(@RequestBody CategoryMetadataFieldValuesDto categoryMetadataFieldValuesDto)
    {
        return categoryService.addCategoryMetadataFieldValues(categoryMetadataFieldValuesDto);
    }

    @PatchMapping("/update-metadata-field-values")
    public ResponseEntity<String> updateMetadataFieldValues(@RequestBody CategoryMetadataFieldValuesDto categoryMetadataFieldValuesDto)
    {
        return categoryService.updateCategoryMetadataFieldValues(categoryMetadataFieldValuesDto);
    }

}
