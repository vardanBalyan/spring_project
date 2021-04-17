package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.entities.Address;
import com.ttn.bootcampProject.entities.Customer;
import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.entities.orders.OrderProduct;
import com.ttn.bootcampProject.entities.orders.OrderStatus;
import com.ttn.bootcampProject.entities.orders.Orders;
import com.ttn.bootcampProject.entities.products.ProductVariation;
import com.ttn.bootcampProject.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

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


    public ResponseEntity<String> orderProductsFromCart(String email)
    {
        // getting the customer from principle
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());

        List<Long> productVariationIds = cartRepository.getListOfProductVariationIdForCustomerId(customer.getId());

        List<ProductVariation> productVariationList = productVariationRepository
                .getAllProductVariationByIdsList(productVariationIds);

        Orders order = new Orders();
        order.setCustomer(customer);
        order.setDateCreated(new Date());
        order.setPaymentMethod("COD");

        Address customerAddress = addressRepository.findAddressByUserIdAndLabel(customer.getId(), "home");

        order.setCustomerAddressAddressLine(customerAddress.getAddressLine());
        order.setCustomerAddressCity(customerAddress.getCity());
        order.setCustomerAddressCountry(customerAddress.getCountry());
        order.setCustomerAddressState(customerAddress.getState());
        order.setCustomerAddressZipCode(customerAddress.getZipCode());
        order.setCustomerAddressLabel(customerAddress.getLabel());
        order.setAmountPaid(returnTotalAmount(productVariationList, customer.getId()));

        ordersRepository.save(order);

        for (ProductVariation productVariation: productVariationList) {

            int quantityFromCart = cartRepository.getQuantityForCustomerIdAndVariationId(customer.getId()
                    , productVariation.getId());

            OrderStatus orderProductStatus = new OrderStatus();
            orderProductStatus.setOrders(order);
            orderProductStatus.setProductVariation(productVariation);

            if(productVariation.getQuantityAvailable() == 0)
            {
                return new ResponseEntity("Product variation with id : "+productVariation.getId()
                        +" is out of stock.",HttpStatus.BAD_REQUEST);
            }
            if(productVariation.getQuantityAvailable()<quantityFromCart) {

                return new ResponseEntity("Only "+productVariation.getQuantityAvailable()
                        +" is left for the product variation id : "+productVariation.getId(),HttpStatus.BAD_REQUEST);
            }

            orderProductStatus.setQuantity(quantityFromCart);
            orderProductStatus.setPrice(productVariation.getPrice()*orderProductStatus.getQuantity());
            orderProductStatus.setProductVariationMetadata(productVariation.getMetadata());


            orderProductStatus.setFromStatus(null);
            orderProductStatus.setToStatus(OrderStatus.Status.ORDER_PLACED);

            productVariation.setQuantityAvailable(productVariation.getQuantityAvailable()-orderProductStatus.getQuantity());
            productVariationRepository.save(productVariation);

            orderProductRepository.save(orderProductStatus);       // bcoz OrderStatus inherits OrderProduct.
        }
        return new ResponseEntity<>("Order placed successfully with order id : "+order.getId(),HttpStatus.CREATED);
    }

    private double returnTotalAmount(List<ProductVariation> productVariationList, long customerId)
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
