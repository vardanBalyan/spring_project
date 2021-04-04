package com.ttn.bootcampProject.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryMetadataFieldValuesDto {

    private long categoryId;
    private long metadataId;
    private String values;
}
