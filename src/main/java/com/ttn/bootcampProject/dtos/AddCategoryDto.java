package com.ttn.bootcampProject.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class AddCategoryDto {

    private long id;
    @Size(min = 3, message = "should contain at least {min} characters.")
    private String name;
    private Long parentId;
    private boolean hasChild;
}
