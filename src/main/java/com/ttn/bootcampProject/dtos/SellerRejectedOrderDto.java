package com.ttn.bootcampProject.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SellerRejectedOrderDto {

    private long orderId;
    private String productName;
    private long variationId;
    private long orderProductId;

    @Override
    public String toString() {
        return "SellerRejectedOrderDto{" +
                "orderId=" + orderId +
                ", productName='" + productName + '\'' +
                ", variationId=" + variationId +
                ", orderProductId=" + orderProductId +
                '}';
    }
}
