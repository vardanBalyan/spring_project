package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.dtos.SellerRejectedOrderDto;
import com.ttn.bootcampProject.emailservices.MailService;
import com.ttn.bootcampProject.entities.Seller;
import com.ttn.bootcampProject.entities.orders.OrderProduct;
import com.ttn.bootcampProject.entities.orders.OrderStatus;
import com.ttn.bootcampProject.entities.orders.Orders;
import com.ttn.bootcampProject.entities.products.Product;
import com.ttn.bootcampProject.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
public class SchedulerService {

    @Autowired
    SellerRepository sellerRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductVariationRepository productVariationRepository;
    @Autowired
    OrdersRepository ordersRepository;
    @Autowired
    OrderProductRepository orderProductRepository;
    @Autowired
    MailService mailService;
    @Autowired
    OrderStatusRepository orderStatusRepository;

    @Scheduled(cron = "0 14 13 * * ?") // cron pattern : second, minute, hour, day, month, weekday
    public void sendScheduledMail()
    {

        List<SellerRejectedOrderDto> sellerRejectedOrderDtoList = new ArrayList<>();

        List<Seller> sellerList = (List<Seller>) sellerRepository.findAll();

        for (Seller seller: sellerList) {
            List<Long> sellerProductIds = productRepository.getAllProductIdsForSellerId(seller.getId());

            List<Long> productVariationIds = productVariationRepository
                    .getAllVariationIdsForListOfProductId(sellerProductIds);


            List<Long> orderIds = orderProductRepository.getAllOrderIdForVariationIdsList(productVariationIds);


            List<Orders> sellerOrders = ordersRepository.findByIdIn(orderIds);

            for (Orders orders: sellerOrders) {

                List<OrderProduct> orderProductList = orderProductRepository
                        .findByOrderIdAndVariationIdList(productVariationIds, orders.getId());

                for (OrderProduct orderProduct: orderProductList) {
                    OrderStatus orderStatus = orderStatusRepository.findById(orderProduct.getId());

                    if(orderStatus.getToStatus().equals(OrderStatus.Status.ORDER_REJECTED))
                    {

                        Product product = productRepository
                                .findById(productVariationRepository
                                        .getProductIdForVariationId(orderProduct.getProductVariation()
                                                .getId()));

                        SellerRejectedOrderDto sellerRejectedOrderDto = new SellerRejectedOrderDto();
                        sellerRejectedOrderDto.setOrderId(orders.getId());
                        sellerRejectedOrderDto.setVariationId(orderProduct.getProductVariation().getId());
                        sellerRejectedOrderDto.setProductName(product.getName());

                        sellerRejectedOrderDtoList.add(sellerRejectedOrderDto);
                    }
                }
            }


            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(seller.getEmail());
            mailMessage.setSubject("Rejected Orders");
            mailMessage.setFrom("vardanbalyan97@gmail.com");
            mailMessage.setText("To confirm your account, please click here : "
                    +"http://localhost:8080/confirm-account?token=");

            sellerRejectedOrderDtoList.clear();
            mailService.sendRegisterMail(mailMessage);
        }

    }
}
