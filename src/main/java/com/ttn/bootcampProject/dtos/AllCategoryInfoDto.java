package com.ttn.bootcampProject.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AllCategoryInfoDto {

    private long id;
    private String categoryName;
    private String parentChain;
    private List<DisplayCategoryDto> childList;
}
