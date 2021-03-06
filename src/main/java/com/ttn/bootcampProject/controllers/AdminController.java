package com.ttn.bootcampProject.controllers;

import com.ttn.bootcampProject.dtos.*;
import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.entities.orders.OrderStatus;
import com.ttn.bootcampProject.entities.products.categories.CategoryMetadataField;
import com.ttn.bootcampProject.exceptions.CategoryNotFoundException;
import com.ttn.bootcampProject.exceptions.EnumValueNotFoundException;
import com.ttn.bootcampProject.exceptions.OrderNotFoundException;
import com.ttn.bootcampProject.services.CategoryService;
import com.ttn.bootcampProject.services.OrderService;
import com.ttn.bootcampProject.services.ProductService;
import com.ttn.bootcampProject.services.UserDaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @Autowired
    ProductService productService;
    @Autowired
    OrderService orderService;

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
    public AllCategoryInfoDto viewACategory(@PathVariable long id)
    {
        AllCategoryInfoDto categoryDto = categoryService.viewACategory(id);
        if(categoryDto == null)
        {
            throw new CategoryNotFoundException("No category found for the specified category id.");
        }
        return categoryDto;
    }

    @GetMapping("/category")
    public List<AllCategoryInfoDto> viewAllCategory()
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
    public ResponseEntity<String> addMetadataFieldValues(
            @Valid @RequestBody AddCategoryMetadataFieldValuesDto addCategoryMetadataFieldValuesDto)
    {
        return categoryService.addCategoryMetadataFieldValues(addCategoryMetadataFieldValuesDto);
    }

    @PatchMapping("/update-metadata-field-values")
    public ResponseEntity<String> updateMetadataFieldValues(
            @Valid @RequestBody AddCategoryMetadataFieldValuesDto addCategoryMetadataFieldValuesDto)
    {
        return categoryService.updateCategoryMetadataFieldValues(addCategoryMetadataFieldValuesDto);
    }



    @PatchMapping("/deactivate-product/{id}")
    public ResponseEntity<String> deactivateAProduct(@PathVariable long id)
    {
        return productService.deactivateAProduct(id);
    }

    @PatchMapping("/activate-product/{id}")
    public ResponseEntity<String> activateAProduct(@PathVariable long id)
    {
        return productService.activateAProduct(id);
    }

    @GetMapping("/product/{id}")
    public ProductWithVariationImageDto viewAProduct(@PathVariable long id)
    {
        return productService.viewAProductForAdmin(id);
    }

    @GetMapping("/product")
    public List<ProductWithVariationImageDto> viewAllProduct()
    {
        return productService.viewAllProductForAdmin();
    }

    @GetMapping("/view-all-orders")
    public List<DisplayOrderDto> viewALlOrders(@RequestParam(required = false) Integer page)
    {
        return orderService.viewAllOrderForAdmin(page);
    }


    @PatchMapping("/change-status-of-order")
    public ResponseEntity<String> changeStatusOfOrder(@RequestParam long orderProductId
            , @RequestParam String fromStatus, @RequestParam String toStatus)
    {
        try {
            return orderService.changeStatusOfOrderForAdmin(OrderStatus.Status.valueOf(fromStatus)
                    ,OrderStatus.Status.valueOf(toStatus),orderProductId);
        }catch (IllegalArgumentException e)
        {
            throw new EnumValueNotFoundException(e.getMessage());
        }
    }
}
