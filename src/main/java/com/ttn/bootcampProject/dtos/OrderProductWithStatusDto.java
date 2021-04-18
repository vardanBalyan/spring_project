package com.ttn.bootcampProject.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class OrderProductWithStatusDto {

    private long variationId;
    private String name;
    private Map<String,String> metadata;
    private double price;
    private int quantity;
    private String status;
}
