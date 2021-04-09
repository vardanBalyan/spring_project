package com.ttn.bootcampProject.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FilterCategoryDto {

    private Map<String,String> metadata;
    private List<String> brands;
    private double minPrice;
    private double maxPrice;
}
