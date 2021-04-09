package com.ttn.bootcampProject.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DisplayProductVariationForCustomerDto {

    private long id;
    private Double price;
    private long quantityAvailable;
    private String primaryImageName;
    private boolean Active;
    private Map<String,String> metadata;
}
