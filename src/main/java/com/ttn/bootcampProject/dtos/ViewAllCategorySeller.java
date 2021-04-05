package com.ttn.bootcampProject.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ViewAllCategorySeller {

    private String categoryName;
    private Map<String,String> metadataFieldsAndValues;
}
