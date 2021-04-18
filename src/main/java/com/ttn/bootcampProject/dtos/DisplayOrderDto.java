package com.ttn.bootcampProject.dtos;

import com.ttn.bootcampProject.entities.Address;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class DisplayOrderDto {

    private long orderId;
    private double totalAmount;
    private String paymentMethod;
    private Date creationDate;
    private Address address;
    private List<OrderProductWithStatusDto> products;
}
