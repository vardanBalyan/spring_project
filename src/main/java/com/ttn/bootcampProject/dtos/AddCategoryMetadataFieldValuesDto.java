package com.ttn.bootcampProject.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddCategoryMetadataFieldValuesDto {

    private List<CategoryMetadataFieldValuesDto> categoryMetadataFieldValuesDtoList;
}
