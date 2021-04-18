package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.entities.Address;
import com.ttn.bootcampProject.entities.Customer;
import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.entities.orders.Cart;
import com.ttn.bootcampProject.entities.orders.OrderProduct;
import com.ttn.bootcampProject.entities.orders.OrderStatus;
import com.ttn.bootcampProject.entities.orders.Orders;
import com.ttn.bootcampProject.entities.products.ProductVariation;
import com.ttn.bootcampProject.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.prefs.BackingStoreException;

import static com.ttn.bootcampProject.entities.orders.OrderStatus.Status.*;

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
    @Autowired
    OrderStatusRepository orderStatusRepository;

    @Transactional
    public ResponseEntity<String> orderProductsFromCart(String email)
    {
        // getting the customer from principle
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());

        // getting all ids for production variation from customer's cart
        List<Long> productVariationIds = cartRepository.getListOfProductVariationIdForCustomerId(customer.getId());

        // list of production variation from customer's cart
        List<ProductVariation> productVariationList = new ArrayList<>();

        for (Long variationId: productVariationIds) {

            ProductVariation productVariation = productVariationRepository.findProductVariationById(variationId);

            // checking if product variation is deleted
            if(productVariation == null)
            {
                return new ResponseEntity("Product variation with id : "+variationId
                        +" not found at time of ordering.",HttpStatus.NOT_FOUND);
            }

            // checking if product variation is active
            if(!productVariation.isActive())
            {
                return new ResponseEntity("Product variation with id: "+productVariation.getId()+" is inactive."
                        ,HttpStatus.BAD_REQUEST);
            }

            productVariationList.add(productVariation);
        }

        // creating new instance of orders
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
            orderProductStatus.setToStatus(ORDER_PLACED);

            // updating the product variation quantity after the customers orders certain quantity
            productVariation.setQuantityAvailable(productVariation.getQuantityAvailable()-orderProductStatus.getQuantity());
            productVariationRepository.save(productVariation);

            orderProductRepository.save(orderProductStatus);       // bcoz OrderStatus inherits OrderProduct.
        }

        // empting the cart after order creation
        cartService.emptyCart(customer.getEmail());

        return new ResponseEntity<>("Order placed successfully with order id : "+order.getId(),HttpStatus.CREATED);
    }

    public ResponseEntity<String> orderPartialProductsFromCart(List<Long> productVariationIds, String email)
    {
        // getting the customer from principle
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());

        // getting all ids for production variation from customer's cart
        List<Long> productVariationIdsFromCart = cartRepository.getListOfProductVariationIdForCustomerId(customer.getId());

        // list for production variation from customer's cart
        List<ProductVariation> productVariationList = new ArrayList<>();

        for (Long variationId: productVariationIds) {

            // checking if passed product variation id exist in cart
            if(!productVariationIdsFromCart.contains(variationId))
            {
                return new ResponseEntity("Product variation with id: "+variationId+" does not exist in your cart"
                        , HttpStatus.NOT_FOUND);
            }
        }

        for (Long variationId: productVariationIds) {

            ProductVariation productVariation = productVariationRepository.findProductVariationById(variationId);

            // checking if product variation is deleted
            if(productVariation == null)
            {
                return new ResponseEntity("Product variation with id : "+variationId
                        +" not found at time of ordering.",HttpStatus.NOT_FOUND);
            }

            // checking if product variation is active
            if(!productVariation.isActive())
            {
                return new ResponseEntity("Product variation with id: "+productVariation.getId()+" is inactive."
                        ,HttpStatus.BAD_REQUEST);
            }

            productVariationList.add(productVariation);
        }

        // creating new instance of orders
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
            orderProductStatus.setToStatus(ORDER_PLACED);

            // updating the product variation quantity after the customers orders certain quantity
            productVariation.setQuantityAvailable(productVariation.getQuantityAvailable()-orderProductStatus.getQuantity());
            productVariationRepository.save(productVariation);

            orderProductRepository.save(orderProductStatus);       // bcoz OrderStatus inherits OrderProduct.
        }

        // deleting the ordered item from the cart after order creation
        for (Long variationId: productVariationIds) {
            Cart cartItem = cartRepository.getItemFromCartForCompositeKeyCombination(customer.getId(), variationId);
            cartRepository.delete(cartItem);
        }

        return new ResponseEntity<>("Order placed successfully with order id : "+order.getId(),HttpStatus.CREATED);
    }

    public ResponseEntity<String> directOrderProduct(Long productVariationId, Integer quantity, String email)
    {
        // getting the customer from principle
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());

        ProductVariation productVariation = productVariationRepository.findProductVariationById(productVariationId);

        if(productVariation == null)
        {
            return new ResponseEntity("Invalid product variation id",HttpStatus.NOT_FOUND);
        }

        // checking if product variation is active
        if(!productVariation.isActive())
        {
            return new ResponseEntity("Product variation with id: "+productVariation.getId()+" is inactive."
                    ,HttpStatus.BAD_REQUEST);
        }

        if(quantity<=0)
        {
            return new ResponseEntity("Quantity should be greater than 0.",HttpStatus.BAD_REQUEST);
        }

        if(productVariation.getQuantityAvailable() == 0)
        {
            return new ResponseEntity("Product variation is out of stock.",HttpStatus.BAD_REQUEST);
        }

        if(productVariation.getQuantityAvailable() < quantity)
        {
            return new ResponseEntity("Only "+ productVariation.getQuantityAvailable()
                    +" stocks available for the product variations.",HttpStatus.BAD_REQUEST);
        }

        // creating new instance of orders
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
        order.setAmountPaid(productVariation.getPrice()*quantity);

        // saving the order
        ordersRepository.save(order);

        // creating instance of order status to persist data in OrderProduct and OrderStatus
        // since OrderStatus is inheriting OrderProduct, so using the instance of OrderStatus
        // to also set the OrderProduct entity
        OrderStatus orderProductStatus = new OrderStatus();
        orderProductStatus.setOrders(order);
        orderProductStatus.setProductVariation(productVariation);
        orderProductStatus.setQuantity(quantity);
        orderProductStatus.setPrice(productVariation.getPrice()*orderProductStatus.getQuantity());
        orderProductStatus.setProductVariationMetadata(productVariation.getMetadata());

        // setting order status from null bcoz order is just created and didn't had a state before creation
        orderProductStatus.setFromStatus(null);
        orderProductStatus.setToStatus(ORDER_PLACED);

        // updating the product variation quantity after the customers orders certain quantity
        productVariation.setQuantityAvailable(productVariation.getQuantityAvailable()-orderProductStatus.getQuantity());
        productVariationRepository.save(productVariation);

        orderProductRepository.save(orderProductStatus);

        return new ResponseEntity<>("Order placed successfully with order id : "+order.getId(),HttpStatus.CREATED);
    }


    public ResponseEntity<String> cancelOrder(String email, long orderProductId)
    {
        // getting the customer from principle
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());

        OrderProduct orderProduct = orderProductRepository.findById(orderProductId);

        if(orderProduct == null)
        {
            return new ResponseEntity("Invalid orderProduct id",HttpStatus.NOT_FOUND);
        }

        long orderIdForOrderProductId = orderProductRepository.getOrderIdForOrderProductId(orderProduct.getId());

        Orders orders = ordersRepository.findById(orderIdForOrderProductId);

        if(customer.getId() != orders.getCustomer().getId())
        {
            return new ResponseEntity("You don't have any product with specific orderProduct id."
                    ,HttpStatus.NOT_FOUND);
        }

        OrderStatus orderStatus = orderStatusRepository.findById(orderProduct.getId());

        if(!orderStatus.getToStatus().equals(ORDER_PLACED))
        {
            return new ResponseEntity("Cannot cancel order. Order is in "+orderStatus.getToStatus()+" state."
                    ,HttpStatus.BAD_REQUEST);
        }

        orderStatus.setFromStatus(orderStatus.getToStatus());
        orderStatus.setToStatus(CANCELLED);
        orderStatus.setTransitionNotesComments("Order cancelled by customer.");

        orderStatusRepository.save(orderStatus);
        return new ResponseEntity("Your order is now cancelled.",HttpStatus.ACCEPTED);
    }


    public ResponseEntity<String> returnOrder(String email, long orderProductId)
    {
        // getting the customer from principle
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());

        OrderProduct orderProduct = orderProductRepository.findById(orderProductId);

        if(orderProduct == null)
        {
            return new ResponseEntity("Invalid orderProduct id",HttpStatus.NOT_FOUND);
        }

        long orderIdForOrderProductId = orderProductRepository.getOrderIdForOrderProductId(orderProduct.getId());

        Orders orders = ordersRepository.findById(orderIdForOrderProductId);

        if(customer.getId() != orders.getCustomer().getId())
        {
            return new ResponseEntity("You don't have any product with specific orderProduct id."
                    ,HttpStatus.NOT_FOUND);
        }

        OrderStatus orderStatus = orderStatusRepository.findById(orderProduct.getId());

        if(!orderStatus.getToStatus().equals(DELIVERED))
        {
            return new ResponseEntity("Cannot place return request for order. Order is in "
                    +orderStatus.getToStatus()+" state.",HttpStatus.BAD_REQUEST);
        }

        orderStatus.setFromStatus(orderStatus.getToStatus());
        orderStatus.setToStatus(RETURN_REQUESTED);
        orderStatus.setTransitionNotesComments("Return requested by customer.");

        orderStatusRepository.save(orderStatus);
        return new ResponseEntity("Return request placed successfully.",HttpStatus.ACCEPTED);
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
