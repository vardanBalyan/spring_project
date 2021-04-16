package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.dtos.CartDto;
import com.ttn.bootcampProject.dtos.DisplayCartDto;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        ProductVariation productVariation = productVariationRepository.findProductVariationById(cartDto.getVariationId());

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

        Cart item = cartRepository.getItemFromCartForCompositeKeyCombination(customer.getId(), productVariation.getId());

        // if item already exist then increment the quantity
        if(item != null)
        {
            if(productVariation.getQuantityAvailable()<item.getQuantity()+ cartDto.getQuantity())
            {
                return new ResponseEntity("Only "+productVariation.getQuantityAvailable()
                        +" quantity left for the product variation. You already have "+item.getQuantity()+" in your cart."
                        ,HttpStatus.BAD_REQUEST);
            }
            item.setQuantity(item.getQuantity()+ cartDto.getQuantity());
            cartRepository.save(item);
            return new ResponseEntity("Product variation successfully added to the cart.",HttpStatus.ACCEPTED);
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


    public List<DisplayCartDto> viewCart(String email)
    {
        // getting the customer from principle
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());

        List<DisplayCartDto> displayCartDtoList = new ArrayList<>();

        List<Cart> allItems = cartRepository.getAllCartItemsForCustomerId(customer.getId());

        for (Cart item: allItems) {
            ProductVariation productVariation = productVariationRepository
                    .findProductVariationById(item.getCartId().getProductVariationId());

            Product product = productRepository.findById(productVariationRepository
                    .getProductIdForVariationId(productVariation.getId()));

            DisplayCartDto displayCartDto = new DisplayCartDto();

            displayCartDto.setMetadata(productVariation.getMetadata());
            displayCartDto.setProductName(product.getName());
            displayCartDto.setQuantity(item.getQuantity());
            displayCartDto.setVariationId(item.getCartId().getProductVariationId());

            displayCartDtoList.add(displayCartDto);
        }

        return displayCartDtoList;
    }


    public ResponseEntity<String> deleteProductFromCart(long variationId, String email)
    {
        // getting the customer from principle
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());

        // getting product variation for the variation id provided
        ProductVariation productVariation = productVariationRepository.findProductVariationById(variationId);

        // checking for valid variation
        if(productVariation == null)
        {
            return new ResponseEntity("No product variation found for provided variation id."
                    ,HttpStatus.NOT_FOUND);
        }

        // getting the item from the cart
        Cart cartItem = cartRepository.getItemFromCartForCompositeKeyCombination(
                customer.getId(), productVariation.getId());

        // checking if item exist in customer cart for the provided variation id
        if(cartItem == null)
        {
            return new ResponseEntity("No item found in your cart with provided variation id."
                    ,HttpStatus.NOT_FOUND);
        }

        // deleting the item from the cart
        cartRepository.delete(cartItem);
        return new ResponseEntity("Item deleted successfully from cart.",HttpStatus.ACCEPTED);
    }


    public ResponseEntity<String> updateProductInCart(CartDto cartDto, String email)
    {
        // getting the customer from principle
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());

        // getting product variation for the variation id provided
        ProductVariation productVariation = productVariationRepository.findProductVariationById(cartDto.getVariationId());

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

        // getting the item from cart
        Cart fetchedItem = cartRepository.getItemFromCartForCompositeKeyCombination(
                customer.getId(), productVariation.getId());

        // checking if item exist in customer cart for the provided variation id
        if (fetchedItem == null)
        {
            return new ResponseEntity("No product found in your cart for provided variation id"
                    ,HttpStatus.NOT_FOUND);
        }

        // checking if quantity value is negative value
        if(cartDto.getQuantity() < 0)
        {
            return new ResponseEntity("Quantity should be greater than 0 or equal to 0.",HttpStatus.BAD_REQUEST);
        }

        // if quantity value is 0 then delete product from cart
        if(cartDto.getQuantity() == 0)
        {
            cartRepository.delete(fetchedItem);
            return new ResponseEntity("Product successfully removed from the cart.",HttpStatus.ACCEPTED);
        }

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

        // updating quantity
        fetchedItem.setQuantity(cartDto.getQuantity());
        cartRepository.save(fetchedItem);
        return new ResponseEntity("Product updated successfully in cart.",HttpStatus.ACCEPTED);
    }


    @Transactional
    public ResponseEntity<String> emptyCart(String email)
    {
        // getting the customer from principle
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());

        // getting list of all variation ids for the logged in customer from the cart entity
        List<Long> variationIdListInCart = cartRepository.getListOfProductVariationIdForCustomerId(customer.getId());

        // performing delete operation by passing the customer id of logged customer and list of variations ids
        // that we got earlier.
        cartRepository.deleteCustomerItemsFromCart(customer.getId(), variationIdListInCart);
        return new ResponseEntity("Your cart is now empty.",HttpStatus.ACCEPTED);
    }
}
