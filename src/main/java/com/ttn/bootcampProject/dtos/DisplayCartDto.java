package com.ttn.bootcampProject.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DisplayCartDto {

    private String productName;
    private long variationId;
    private Map<String,String> metadata;
    private int quantity;
}
