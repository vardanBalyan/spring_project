package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.dtos.CartDto;
import com.ttn.bootcampProject.entities.Customer;
import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.entities.orders.Cart;
import com.ttn.bootcampProject.entities.orders.CartId;
import com.ttn.bootcampProject.entities.products.Product;
import com.ttn.bootcampProject.entities.products.ProductVariation;
import com.ttn.bootcampProject.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@Repository
public class CartService {

    @Autowired
    ProductVariationRepository productVariationRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartRepository cartRepository;

    public ResponseEntity<String> addProductToCart(CartDto cartDto, String email)
    {
        // getting the customer from principle
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());

        // getting product variation for the variation id provided
        ProductVariation productVariation = productVariationRepository.findById(cartDto.getVariationId());

        // checking for valid variation
        if(productVariation == null)
        {
            return new ResponseEntity("No product variation found for provided variation id."
                    ,HttpStatus.NOT_FOUND);
        }

        // if product variation is active
        if(!productVariation.isActive())
        {
            return new ResponseEntity("Product variation is not active.",HttpStatus.BAD_REQUEST);
        }

        // getting product from the variation
        Product product = productRepository.findById(productVariationRepository
                .getProductIdForVariationId(productVariation.getId()));

        // checking if product is deleted
        if(product.isDeleted())
        {
            return new ResponseEntity("Product is deleted product.",HttpStatus.BAD_REQUEST);
        }

        if(cartDto.getQuantity() < 0)
        {
            return new ResponseEntity("Quantity should be greater than 0.",HttpStatus.BAD_REQUEST);
        }

        // instance for the cart entity
        Cart cart = new Cart();

        // assigning the composite key of cart entity
        CartId cartId = new CartId();
        cartId.setProductVariationId(productVariation.getId());
        cartId.setCustomerUserId(customer.getId());
        cart.setCartId(cartId);

        // checking for the available quantity with the requested quantity
        if(productVariation.getQuantityAvailable() < cartDto.getQuantity())
        {
            // checking if product variation is out of stock
            if(productVariation.getQuantityAvailable() == 0)
            {
                return new ResponseEntity("This variation is out of stock.",HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity("Only "+productVariation.getQuantityAvailable()
                    +" quantity left for the product variation."
                    ,HttpStatus.BAD_REQUEST);
        }

        // setting quantity in cart entry
        cart.setQuantity(cartDto.getQuantity());
        cart.setCustomer(customer);
        cart.setProductVariation(productVariation);
        cartRepository.save(cart);
        return new ResponseEntity("Product variation successfully added to the cart.",HttpStatus.ACCEPTED);
    }
}
