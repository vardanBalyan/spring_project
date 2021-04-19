package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.dtos.DisplayOrderDto;
import com.ttn.bootcampProject.dtos.OrderProductWithStatusDto;
import com.ttn.bootcampProject.entities.Address;
import com.ttn.bootcampProject.entities.Customer;
import com.ttn.bootcampProject.entities.Seller;
import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.entities.orders.Cart;
import com.ttn.bootcampProject.entities.orders.OrderProduct;
import com.ttn.bootcampProject.entities.orders.OrderStatus;
import com.ttn.bootcampProject.entities.orders.Orders;
import com.ttn.bootcampProject.entities.products.Product;
import com.ttn.bootcampProject.entities.products.ProductVariation;
import com.ttn.bootcampProject.exceptions.OrderNotFoundException;
import com.ttn.bootcampProject.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    @Autowired
    ProductRepository productRepository;
    @Autowired
    SellerRepository sellerRepository;

    private int PAGE_SIZE = 5;

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


    public DisplayOrderDto viewOrder(String email, long orderId)
    {
        // getting the customer from principle
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());

        DisplayOrderDto displayOrderDto = new DisplayOrderDto();

        // getting order from order id
        Orders orders = ordersRepository.findById(orderId);

        // checking for valid order id
        if (orders == null)
        {
            throw new OrderNotFoundException("Invalid Order id.");
        }

        // checking if order belong to currently logged in customer
        if(customer.getId() != orders.getCustomer().getId())
        {
            throw new OrderNotFoundException("No order found in your order list with id: "+orderId);
        }

        // setting dto values
        displayOrderDto.setOrderId(orders.getId());
        displayOrderDto.setCreationDate(orders.getDateCreated());
        displayOrderDto.setPaymentMethod(orders.getPaymentMethod());
        displayOrderDto.setTotalAmount(orders.getAmountPaid());

        Address customerAddress = new Address();
        customerAddress.setAddressLine(orders.getCustomerAddressAddressLine());
        customerAddress.setCity(orders.getCustomerAddressCity());
        customerAddress.setCountry(orders.getCustomerAddressCountry());
        customerAddress.setLabel(orders.getCustomerAddressLabel());
        customerAddress.setState(orders.getCustomerAddressState());
        customerAddress.setZipCode(orders.getCustomerAddressZipCode());

        displayOrderDto.setAddress(customerAddress);

        // list to be passed in displayOrderDto
        List<OrderProductWithStatusDto> orderProductWithStatusDtoList = new ArrayList<>();

        // getting all order products for the order
        List<OrderProduct> orderProductList = orderProductRepository.findOrderProductForOrderId(orders.getId());

        for (OrderProduct orderProduct: orderProductList) {

            // getting product variation for particular orderProduct
            Product product = productRepository
                    .findById(productVariationRepository
                            .getProductIdForVariationId(orderProduct.getProductVariation()
                                    .getId()));

            // getting the status of particular order product
            OrderStatus orderStatus = orderStatusRepository.findById(orderProduct.getId());
            OrderProductWithStatusDto orderProductWithStatusDto = new OrderProductWithStatusDto();

            orderProductWithStatusDto.setStatus(orderStatus.getToStatus().toString());
            orderProductWithStatusDto.setMetadata(orderProduct.getProductVariationMetadata());
            orderProductWithStatusDto.setPrice(orderProduct.getPrice());
            orderProductWithStatusDto.setQuantity(orderProduct.getQuantity());
            orderProductWithStatusDto.setVariationId(orderProduct.getProductVariation().getId());
            orderProductWithStatusDto.setName(product.getName());

            // adding to the list of orderProductWithStatusDto that will be passed in displayOrderDto
            orderProductWithStatusDtoList.add(orderProductWithStatusDto);
        }

        // setting the orderProductWithStatusDtoList to displayOrderDto
        displayOrderDto.setProducts(orderProductWithStatusDtoList);
        return displayOrderDto;
    }



    public List<DisplayOrderDto> viewAllOrderForCustomer(String email)
    {
        // getting the customer from principle
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());

        List<DisplayOrderDto> displayOrderDtoList = new ArrayList<>();

        // getting list of orders for the logged in customer
        List<Orders> customerOrders = ordersRepository.findAllOrderForCustomerId(customer.getId());

        for (Orders orders: customerOrders) {

            DisplayOrderDto displayOrderDto = new DisplayOrderDto();

            // setting dto values
            displayOrderDto.setOrderId(orders.getId());
            displayOrderDto.setCreationDate(orders.getDateCreated());
            displayOrderDto.setPaymentMethod(orders.getPaymentMethod());
            displayOrderDto.setTotalAmount(orders.getAmountPaid());

            Address customerAddress = new Address();
            customerAddress.setAddressLine(orders.getCustomerAddressAddressLine());
            customerAddress.setCity(orders.getCustomerAddressCity());
            customerAddress.setCountry(orders.getCustomerAddressCountry());
            customerAddress.setLabel(orders.getCustomerAddressLabel());
            customerAddress.setState(orders.getCustomerAddressState());
            customerAddress.setZipCode(orders.getCustomerAddressZipCode());

            displayOrderDto.setAddress(customerAddress);

            // list to be passed in displayOrderDto
            List<OrderProductWithStatusDto> orderProductWithStatusDtoList = new ArrayList<>();

            // getting all order products for the order
            List<OrderProduct> orderProductList = orderProductRepository.findOrderProductForOrderId(orders.getId());

            for (OrderProduct orderProduct: orderProductList) {

                // getting product variation for particular orderProduct
                Product product = productRepository
                        .findById(productVariationRepository
                                .getProductIdForVariationId(orderProduct.getProductVariation()
                                        .getId()));

                // getting the status of particular order product
                OrderStatus orderStatus = orderStatusRepository.findById(orderProduct.getId());
                OrderProductWithStatusDto orderProductWithStatusDto = new OrderProductWithStatusDto();

                orderProductWithStatusDto.setStatus(orderStatus.getToStatus().toString());
                orderProductWithStatusDto.setMetadata(orderProduct.getProductVariationMetadata());
                orderProductWithStatusDto.setPrice(orderProduct.getPrice());
                orderProductWithStatusDto.setQuantity(orderProduct.getQuantity());
                orderProductWithStatusDto.setVariationId(orderProduct.getProductVariation().getId());
                orderProductWithStatusDto.setName(product.getName());

                // adding to the list of orderProductWithStatusDto that will be passed in displayOrderDto
                orderProductWithStatusDtoList.add(orderProductWithStatusDto);
            }

            // setting the orderProductWithStatusDtoList to displayOrderDto
            displayOrderDto.setProducts(orderProductWithStatusDtoList);

            // adding the displayOrderDto to list
            displayOrderDtoList.add(displayOrderDto);
        }

        return displayOrderDtoList;
    }


    public List<DisplayOrderDto> viewAllOrderForAdmin(Integer page)
    {

        if(page == null)
        {
            page = 0;
        }

        List<DisplayOrderDto> displayOrderDtoList = new ArrayList<>();

        PageRequest pageable = PageRequest.of(page,PAGE_SIZE, Sort.Direction.ASC,"id");
        // getting list of orders for the logged in customer
        List<Orders> customerOrders = ordersRepository.findAllOrders(pageable);

        for (Orders orders: customerOrders) {

            DisplayOrderDto displayOrderDto = new DisplayOrderDto();

            // setting dto values
            displayOrderDto.setOrderId(orders.getId());
            displayOrderDto.setCreationDate(orders.getDateCreated());
            displayOrderDto.setPaymentMethod(orders.getPaymentMethod());
            displayOrderDto.setTotalAmount(orders.getAmountPaid());

            Address customerAddress = new Address();
            customerAddress.setAddressLine(orders.getCustomerAddressAddressLine());
            customerAddress.setCity(orders.getCustomerAddressCity());
            customerAddress.setCountry(orders.getCustomerAddressCountry());
            customerAddress.setLabel(orders.getCustomerAddressLabel());
            customerAddress.setState(orders.getCustomerAddressState());
            customerAddress.setZipCode(orders.getCustomerAddressZipCode());

            displayOrderDto.setAddress(customerAddress);

            // list to be passed in displayOrderDto
            List<OrderProductWithStatusDto> orderProductWithStatusDtoList = new ArrayList<>();

            // getting all order products for the order
            List<OrderProduct> orderProductList = orderProductRepository.findOrderProductForOrderId(orders.getId());

            for (OrderProduct orderProduct: orderProductList) {

                // getting product variation for particular orderProduct
                Product product = productRepository
                        .findById(productVariationRepository
                                .getProductIdForVariationId(orderProduct.getProductVariation()
                                        .getId()));

                // getting the status of particular order product
                OrderStatus orderStatus = orderStatusRepository.findById(orderProduct.getId());
                OrderProductWithStatusDto orderProductWithStatusDto = new OrderProductWithStatusDto();

                orderProductWithStatusDto.setStatus(orderStatus.getToStatus().toString());
                orderProductWithStatusDto.setMetadata(orderProduct.getProductVariationMetadata());
                orderProductWithStatusDto.setPrice(orderProduct.getPrice());
                orderProductWithStatusDto.setQuantity(orderProduct.getQuantity());
                orderProductWithStatusDto.setVariationId(orderProduct.getProductVariation().getId());
                orderProductWithStatusDto.setName(product.getName());

                // adding to the list of orderProductWithStatusDto that will be passed in displayOrderDto
                orderProductWithStatusDtoList.add(orderProductWithStatusDto);
            }

            // setting the orderProductWithStatusDtoList to displayOrderDto
            displayOrderDto.setProducts(orderProductWithStatusDtoList);

            // adding the displayOrderDto to list
            displayOrderDtoList.add(displayOrderDto);
        }

        return displayOrderDtoList;
    }


    public ResponseEntity<String> changeStatusOfOrderForAdmin(OrderStatus.Status fromStatus, OrderStatus.Status toStatus
            , long orderProductId)
    {
        OrderProduct orderProduct = orderProductRepository.findById(orderProductId);

        if(orderProduct == null)
        {
            return new ResponseEntity("Invalid orderProduct id",HttpStatus.NOT_FOUND);
        }

        // getting the status of particular order product
        OrderStatus orderStatus = orderStatusRepository.findById(orderProduct.getId());

        orderStatus.setFromStatus(fromStatus);
        orderStatus.setToStatus(toStatus);

        orderProductRepository.save(orderStatus);

        return new ResponseEntity("Status changed successfully.",HttpStatus.ACCEPTED);
    }

    public List<DisplayOrderDto> viewAllOrderForSeller(String email)
    {
        // finding user by email getting from principal
        User user = userRepository.findByEmail(email);
        // finding the seller from user id we got from email
        Seller seller = sellerRepository.findSellerByUserId(user.getId());

        List<DisplayOrderDto> displayOrderDtoList = new ArrayList<>();

        List<Long> sellerProductIds = productRepository.getAllProductIdsForSellerId(seller.getId());

        List<Long> productVariationIds = productVariationRepository
                .getAllVariationIdsForListOfProductId(sellerProductIds);


        List<Long> orderIds = orderProductRepository.getAllOrderIdForVariationIdsList(productVariationIds);


        List<Orders> customerOrders = ordersRepository.findByIdIn(orderIds);

        for (Orders orders: customerOrders) {

            DisplayOrderDto displayOrderDto = new DisplayOrderDto();

            // setting dto values
            displayOrderDto.setOrderId(orders.getId());
            displayOrderDto.setCreationDate(orders.getDateCreated());
            displayOrderDto.setPaymentMethod(orders.getPaymentMethod());
            displayOrderDto.setTotalAmount(orders.getAmountPaid());

            Address customerAddress = new Address();
            customerAddress.setAddressLine(orders.getCustomerAddressAddressLine());
            customerAddress.setCity(orders.getCustomerAddressCity());
            customerAddress.setCountry(orders.getCustomerAddressCountry());
            customerAddress.setLabel(orders.getCustomerAddressLabel());
            customerAddress.setState(orders.getCustomerAddressState());
            customerAddress.setZipCode(orders.getCustomerAddressZipCode());

            displayOrderDto.setAddress(customerAddress);

            // list to be passed in displayOrderDto
            List<OrderProductWithStatusDto> orderProductWithStatusDtoList = new ArrayList<>();

            // getting all order products for the order
            List<OrderProduct> orderProductList = orderProductRepository
                    .findByOrderIdAndVariationIdList(productVariationIds, orders.getId());

            for (OrderProduct orderProduct: orderProductList) {

                // getting product variation for particular orderProduct
                Product product = productRepository
                        .findById(productVariationRepository
                                .getProductIdForVariationId(orderProduct.getProductVariation()
                                        .getId()));

                // getting the status of particular order product
                OrderStatus orderStatus = orderStatusRepository.findById(orderProduct.getId());
                OrderProductWithStatusDto orderProductWithStatusDto = new OrderProductWithStatusDto();

                orderProductWithStatusDto.setStatus(orderStatus.getToStatus().toString());
                orderProductWithStatusDto.setMetadata(orderProduct.getProductVariationMetadata());
                orderProductWithStatusDto.setPrice(orderProduct.getPrice());
                orderProductWithStatusDto.setQuantity(orderProduct.getQuantity());
                orderProductWithStatusDto.setVariationId(orderProduct.getProductVariation().getId());
                orderProductWithStatusDto.setName(product.getName());

                // adding to the list of orderProductWithStatusDto that will be passed in displayOrderDto
                orderProductWithStatusDtoList.add(orderProductWithStatusDto);
            }

            // setting the orderProductWithStatusDtoList to displayOrderDto
            displayOrderDto.setProducts(orderProductWithStatusDtoList);

            // adding the displayOrderDto to list
            displayOrderDtoList.add(displayOrderDto);
        }
        return displayOrderDtoList;
    }


    public ResponseEntity<String> changeOrderStatusForSeller(String email, long orderProductId
            , OrderStatus.Status fromStatus, OrderStatus.Status toStatus)
    {
        // finding user by email getting from principal
        User user = userRepository.findByEmail(email);
        // finding the seller from user id we got from email
        Seller seller = sellerRepository.findSellerByUserId(user.getId());

        List<DisplayOrderDto> displayOrderDtoList = new ArrayList<>();

        List<Long> sellerProductIds = productRepository.getAllProductIdsForSellerId(seller.getId());

        List<Long> productVariationIds = productVariationRepository
                .getAllVariationIdsForListOfProductId(sellerProductIds);

        List<Long> orderProductsIds = orderProductRepository.getOrderProductIdsForVariationIdList(productVariationIds);

        if(!orderProductsIds.contains(orderProductId))
        {
            return new ResponseEntity("You are not owner of this product.",HttpStatus.BAD_REQUEST);
        }

        if(!checkTransition(fromStatus,toStatus))
        {
            return new ResponseEntity("Invalid status transition",HttpStatus.BAD_REQUEST);
        }

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>"+checkTransition(fromStatus,toStatus));

        // getting the status of particular order product
        OrderStatus orderStatus = orderStatusRepository.findById(orderProductId);

        orderStatus.setFromStatus(fromStatus);
        orderStatus.setToStatus(toStatus);

        orderProductRepository.save(orderStatus);

        return new ResponseEntity("Status changed successfully.",HttpStatus.ACCEPTED);
    }


    public boolean checkTransition(OrderStatus.Status fromStatus, OrderStatus.Status toStatus)
    {
        List<OrderStatus.Status> statusList;

        if(fromStatus.equals(ORDER_PLACED))
        {
            statusList = Arrays.asList(CANCELLED,ORDER_CONFIRMED,ORDER_REJECTED);

            if(statusList.contains(toStatus))
                return true;

        }

        if(fromStatus.equals(CANCELLED))
        {
            statusList = Arrays.asList(REFUND_INITIATED,CLOSED);

            if(statusList.contains(toStatus))
                return true;

        }

        if(fromStatus.equals(ORDER_REJECTED))
        {
            statusList = Arrays.asList(REFUND_INITIATED,CLOSED);

            if(statusList.contains(toStatus))
                return true;

        }

        if(fromStatus.equals(ORDER_CONFIRMED))
        {
            statusList = Arrays.asList(ORDER_SHIPPED,CANCELLED);

            if(statusList.contains(toStatus))
                return true;

        }

        if(fromStatus.equals(ORDER_SHIPPED))
        {
            statusList = Arrays.asList(DELIVERED);

            if(statusList.contains(toStatus))
                return true;

        }

        if(fromStatus.equals(DELIVERED))
        {
            statusList = Arrays.asList(RETURN_REQUESTED,CLOSED);

            if(statusList.contains(toStatus))
                return true;

        }

        if(fromStatus.equals(RETURN_REQUESTED))
        {
            statusList = Arrays.asList(RETURN_REJECTED,RETURN_APPROVED);

            if(statusList.contains(toStatus))
                return true;

        }

        if(fromStatus.equals(RETURN_REJECTED))
        {
            statusList = Arrays.asList(CLOSED);

            if(statusList.contains(toStatus))
                return true;

        }

        if(fromStatus.equals(RETURN_APPROVED))
        {
            statusList = Arrays.asList(PICK_UP_INITIATED);

            if(statusList.contains(toStatus))
                return true;

        }

        if(fromStatus.equals(PICK_UP_INITIATED))
        {
            statusList = Arrays.asList(PICK_UP_COMPLETED);

            if(statusList.contains(toStatus))
                return true;

        }

        if(fromStatus.equals(PICK_UP_COMPLETED))
        {
            statusList = Arrays.asList(REFUND_INITIATED);

            if(statusList.contains(toStatus))
                return true;

        }

        if(fromStatus.equals(REFUND_INITIATED))
        {
            statusList = Arrays.asList(REFUND_COMPLETED);

            if(statusList.contains(toStatus))
                return true;

        }

        if(fromStatus.equals(REFUND_COMPLETED))
        {
            statusList = Arrays.asList(CLOSED);

            if(statusList.contains(toStatus))
                return true;

        }


        return false;
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
