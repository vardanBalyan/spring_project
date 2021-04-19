package com.ttn.bootcampProject.controllers;

import com.ttn.bootcampProject.dtos.*;
import com.ttn.bootcampProject.entities.Address;
import com.ttn.bootcampProject.entities.orders.OrderStatus;
import com.ttn.bootcampProject.exceptions.EnumValueNotFoundException;
import com.ttn.bootcampProject.exceptions.ProductNotFoundException;
import com.ttn.bootcampProject.services.CategoryService;
import com.ttn.bootcampProject.services.OrderService;
import com.ttn.bootcampProject.services.ProductService;
import com.ttn.bootcampProject.services.SellerDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "/seller")
public class SellerController {

    @Autowired
    SellerDaoService sellerDaoService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    OrderService orderService;

    @GetMapping("/profile")
    public SellerProfileDto customerProfile(Principal principal) {
        return sellerDaoService.getProfile(principal.getName());
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<String> updateCustomerProfile(@Valid @RequestBody SellerProfileDto sellerProfileDto
            , Principal principal)
    {
        return sellerDaoService.updateProfile(sellerProfileDto, principal.getName());
    }

    @PatchMapping("/update-password")
    public ResponseEntity<String> updateCustomerPassword(@Valid @RequestBody UpdatePasswordDto passwordDto
            , Principal principal)
    {
        return sellerDaoService.updatePassword(passwordDto, principal.getName());
    }

    @PatchMapping("/update-address/{id}")
    public ResponseEntity<String> updateAddress(@Valid @RequestBody Address address
            , @PathVariable long id, Principal principal)
    {
        return sellerDaoService.updateAnAddress(address, id, principal.getName());
    }

    @GetMapping("/category")
    public List<DisplayCategoryDto> viewAllCategory()
    {
        return categoryService.viewAllCategoryForSeller();
    }

    @PostMapping("/add-product")
    public ResponseEntity<String> addAProduct(@Valid @RequestBody AddProductDto addProductDto, Principal principal)
    {
        return productService.addAProduct(addProductDto, principal.getName());
    }


    @GetMapping("/product/{id}")
    public DisplayProductDto viewAProduct(@PathVariable long id, Principal principal)
    {
        DisplayProductDto displayProductDto = productService.viewAProduct(id, principal.getName());

        if(displayProductDto == null)
        {
            throw new ProductNotFoundException("No product found for the specific product id.");
        }

        return displayProductDto;
    }

    @GetMapping("/product")
    public List<DisplayProductDto> viewAllProducts(Principal principal)
    {
        return productService.viewALlProducts(principal.getName());
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteAProduct(@PathVariable long id, Principal principal)
    {
        return productService.deleteAProduct(id, principal.getName());
    }

    @PatchMapping("/update-product/{id}")
    public ResponseEntity<String> updateProduct(@Valid @RequestBody AddProductDto updateProduct, @PathVariable long id, Principal principal)
    {
        return productService.updateProduct(updateProduct,id, principal.getName());
    }

    @PostMapping("/add-product-variation")
    public ResponseEntity<String> addProductVariation(@Valid @RequestBody AddProductVariationDto addProductVariationDto, Principal principal)
    {
        return productService.addAProductVariation(addProductVariationDto, principal.getName());
    }

    @GetMapping("/product-variation/{id}")
    public DisplayProductVariationDto viewAProductVariation(@PathVariable long id, Principal principal)
    {
        return productService.viewAProductVariation(id, principal.getName());
    }

    @GetMapping("/product-variation")
    public List<DisplayProductVariationDto> viewAllProductVariation(Principal principal)
    {
        return productService.viewAllProductVariation(principal.getName());
    }

    @PatchMapping("/update-product-variation/{id}")
    public ResponseEntity<String> updateProduct(@RequestBody AddProductVariationDto addProductVariationDto, @PathVariable long id, Principal principal)
    {
        return productService.updateProductVariation(addProductVariationDto, principal.getName(), id);
    }


    @GetMapping("/view-all-orders")
    public List<DisplayOrderDto> viewAllOrders(Principal principal)
    {
        return orderService.viewAllOrderForSeller(principal.getName());
    }

    @PatchMapping("/change-status-of-order")
    public ResponseEntity<String> changeStatusOfOrder(@RequestParam long orderProductId
            , @RequestParam String fromStatus, @RequestParam String toStatus, Principal principal)
    {
        try {
            return orderService.changeOrderStatusForSeller(principal.getName(),orderProductId
                    , OrderStatus.Status.valueOf(fromStatus)
                    ,OrderStatus.Status.valueOf(toStatus));
        }catch (IllegalArgumentException e)
        {
            throw new EnumValueNotFoundException(e.getMessage());
        }
    }
 }
