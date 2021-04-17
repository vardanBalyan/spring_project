package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.entities.Address;
import com.ttn.bootcampProject.entities.Customer;
import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.entities.orders.OrderStatus;
import com.ttn.bootcampProject.entities.orders.Orders;
import com.ttn.bootcampProject.entities.products.ProductVariation;
import com.ttn.bootcampProject.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public class OrderService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    ProductVariationRepository productVariationRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    OrdersRepository ordersRepository;
    @Autowired
    OrderProductRepository orderProductRepository;
    @Autowired
    CartService cartService;

    @Transactional
    public ResponseEntity<String> orderProductsFromCart(String email)
    {
        // getting the customer from principle
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());

        // getting all ids for production variation from customer's cart
        List<Long> productVariationIds = cartRepository.getListOfProductVariationIdForCustomerId(customer.getId());

        // getting production variation from customer's cart
        List<ProductVariation> productVariationList = productVariationRepository
                .getAllProductVariationByIdsList(productVariationIds);

        // creating new instance od orders
        Orders order = new Orders();
        order.setCustomer(customer);
        order.setDateCreated(new Date());
        // by default assigning the method as COD
        order.setPaymentMethod("COD");

        // getting customer's address by default getting home label address
        Address customerAddress = addressRepository.findAddressByUserIdAndLabel(customer.getId(), "home");

        order.setCustomerAddressAddressLine(customerAddress.getAddressLine());
        order.setCustomerAddressCity(customerAddress.getCity());
        order.setCustomerAddressCountry(customerAddress.getCountry());
        order.setCustomerAddressState(customerAddress.getState());
        order.setCustomerAddressZipCode(customerAddress.getZipCode());
        order.setCustomerAddressLabel(customerAddress.getLabel());

        // setting total amount for the order
        order.setAmountPaid(returnOrderTotalAmount(productVariationList, customer.getId()));

        // saving the order
        ordersRepository.save(order);

        for (ProductVariation productVariation: productVariationList) {

            // getting quantity from cart for each customer product variation
            int quantityFromCart = cartRepository.getQuantityForCustomerIdAndVariationId(customer.getId()
                    , productVariation.getId());

            // creating instance of order status to persist data in OrderProduct and OrderStatus
            // since OrderStatus is inheriting OrderProduct, so using the instance of OrderStatus
            // to also set the OrderProduct entity
            OrderStatus orderProductStatus = new OrderStatus();
            orderProductStatus.setOrders(order);
            orderProductStatus.setProductVariation(productVariation);

            // checking if product is out of stock after adding product to cart
            if(productVariation.getQuantityAvailable() == 0)
            {
                return new ResponseEntity("Product variation with id : "+productVariation.getId()
                        +" is out of stock.",HttpStatus.BAD_REQUEST);
            }

            // checking if the product have sufficient quantity available to fulfill the order
            if(productVariation.getQuantityAvailable()<quantityFromCart) {

                return new ResponseEntity("Only "+productVariation.getQuantityAvailable()
                        +" is left for the product variation id : "+productVariation.getId(),HttpStatus.BAD_REQUEST);
            }

            orderProductStatus.setQuantity(quantityFromCart);
            orderProductStatus.setPrice(productVariation.getPrice()*orderProductStatus.getQuantity());
            orderProductStatus.setProductVariationMetadata(productVariation.getMetadata());

            // setting order status from null bcoz order is just created and didn't had a state before creation
            orderProductStatus.setFromStatus(null);
            orderProductStatus.setToStatus(OrderStatus.Status.ORDER_PLACED);

            // updating the product variation quantity after the customers orders certain quantity
            productVariation.setQuantityAvailable(productVariation.getQuantityAvailable()-orderProductStatus.getQuantity());
            productVariationRepository.save(productVariation);

            orderProductRepository.save(orderProductStatus);       // bcoz OrderStatus inherits OrderProduct.
        }

        // empting the cart after order creation
        cartService.emptyCart(customer.getEmail());

        return new ResponseEntity<>("Order placed successfully with order id : "+order.getId(),HttpStatus.CREATED);
    }

    // returns total amount for the order
    private double returnOrderTotalAmount(List<ProductVariation> productVariationList, long customerId)
    {
        double totalAmount = 0;

        for (ProductVariation productVariation: productVariationList) {

            int quantityFromCart = cartRepository.getQuantityForCustomerIdAndVariationId(customerId
                    , productVariation.getId());
            totalAmount+=(productVariation.getPrice()*quantityFromCart);
        }
        return totalAmount;
    }
}
