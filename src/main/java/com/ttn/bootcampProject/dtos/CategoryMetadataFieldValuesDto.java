package com.ttn.bootcampProject.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CategoryMetadataFieldValuesDto {

    private long categoryId;
    private long metadataId;
    @NotNull
    private String values;
}
