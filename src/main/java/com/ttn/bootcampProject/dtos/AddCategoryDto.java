package com.ttn.bootcampProject.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean hasChild;
}
