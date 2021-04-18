package com.ttn.bootcampProject.controllers;

import com.ttn.bootcampProject.dtos.*;
import com.ttn.bootcampProject.entities.Address;
import com.ttn.bootcampProject.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "/customer")
public class CustomerController {

    @Autowired
    CustomerDaoService customerDaoService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    CartService cartService;
    @Autowired
    OrderService orderService;

    @GetMapping("/profile")
    public CustomerProfileDto customerProfile(Principal principal) {
        return customerDaoService.getProfile(principal.getName());
    }

    @GetMapping("/address")
    public List<Address> customerAddresses(Principal principal) {
        return customerDaoService.getAddresses(principal.getName());
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<String> updateCustomerProfile(@Valid @RequestBody CustomerProfileDto customerProfileDto
            , Principal principal)
    {
        return customerDaoService.updateProfile(customerProfileDto, principal.getName());
    }

    @PatchMapping("/update-password")
    public ResponseEntity<String> updateCustomerPassword(@Valid @RequestBody UpdatePasswordDto passwordDto
            , Principal principal)
    {
        return customerDaoService.updatePassword(passwordDto, principal.getName());
    }

    @PatchMapping("/add-address")
    public ResponseEntity<String> addNewAddress(@Valid @RequestBody Address address
            , Principal principal)
    {
        return customerDaoService.addNewAddress(address, principal.getName());
    }

    @PatchMapping("/update-address/{id}")
    public ResponseEntity<String> updateAddress(@Valid @RequestBody Address address
            , @PathVariable long id, Principal principal)
    {
        return customerDaoService.updateAnAddress(address, id, principal.getName());
    }

    @DeleteMapping("/delete-address/{id}")
    public ResponseEntity<String> deleteAnAddress(@PathVariable long id, Principal principal)
    {
        return customerDaoService.deleteAnAddress(id, principal.getName());
    }

    @GetMapping("/category/{id}")
    public List<ViewAllCategoryForCustomerDto> viewAllCategory(@PathVariable Long id)
    {
        return categoryService.viewCategoryForCustomer(id);
    }

    @GetMapping("/filter-category/{id}")
    public FilterCategoryDto filterCategory(@PathVariable long id)
    {
        return categoryService.filterCategory(id);
    }

    @GetMapping("/product/{productId}")
    public DisplayProductForCustomerDto viewAProduct(@PathVariable long productId)
    {
        return productService.viewAProductForCustomer(productId);
    }

    @GetMapping("/all-products/{categoryId}")
    public List<DisplayProductForCustomerDto> viewAllProduct(@PathVariable long categoryId)
    {
        return productService.viewAllProductForCustomer(categoryId);
    }

    @GetMapping("/similar-products/{productId}")
    public List<ProductWithVariationImageDto> viewSimilarProducts(@PathVariable long productId)
    {
        return productService.viewSimilarProducts(productId);
    }

    @PostMapping("/add-product-to-cart")
    public ResponseEntity<String> addProductToCart(@Valid @RequestBody CartDto cartDto, Principal principal)
    {
        return cartService.addProductToCart(cartDto, principal.getName());
    }

    @GetMapping("/view-cart")
    public List<DisplayCartDto> viewCart(Principal principal)
    {
        return cartService.viewCart(principal.getName());
    }

    @DeleteMapping("/delete-from-cart/{variationId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable long variationId, Principal principal)
    {
        return cartService.deleteProductFromCart(variationId, principal.getName());
    }

    @PutMapping("/update-product-in-cart")
    public ResponseEntity<String> updateProductIncCart(@Valid @RequestBody CartDto cartDto, Principal principal)
    {
        return cartService.updateProductInCart(cartDto, principal.getName());
    }

    @DeleteMapping("/empty-cart")
    public ResponseEntity<String> emptyCart(Principal principal)
    {
        return cartService.emptyCart(principal.getName());
    }

    @PostMapping("/order-all-from-cart")
    public ResponseEntity<String> orderFromCart(Principal principal)
    {
        return orderService.orderProductsFromCart(principal.getName());
    }

    @PostMapping("/partial-order-from-cart")
    public ResponseEntity<String> partialOrderFromCart(@RequestParam List<Long> ids, Principal principal)
    {
        return orderService.orderPartialProductsFromCart(ids, principal.getName());
    }

    @PostMapping("/directly-order-product")
    public ResponseEntity<String> directOrderProduct(@RequestParam Long productVariationId
            , @RequestParam Integer quantity, Principal principal)
    {
        if(productVariationId == null)
        {
            return new ResponseEntity("Product variation id should not be null.", HttpStatus.BAD_REQUEST);
        }

        if(quantity == null)
        {
            return new ResponseEntity("Quantity should not be null.",HttpStatus.BAD_REQUEST);
        }

        return orderService.directOrderProduct(productVariationId, quantity, principal.getName());
    }

    @PatchMapping("/cancel-order")
    public ResponseEntity<String> cancelOrder(@RequestParam long orderProductId, Principal principal)
    {
        return orderService.cancelOrder(principal.getName(), orderProductId);
    }

    @PatchMapping("/return-order")
    public ResponseEntity<String> returnOrder(@RequestParam long orderProductId, Principal principal)
    {
        return orderService.returnOrder(principal.getName(), orderProductId);
    }


    @GetMapping("/view-order/{id}")
    public DisplayOrderDto viewOrder(@PathVariable long id, Principal principal)
    {
        return orderService.viewOrder(principal.getName(),id);
    }
}
