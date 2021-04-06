package com.ttn.bootcampProject.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class AddProductDto {

    @Size(min = 2, message = "should contain at least {min} characters.")
    private String name;
    @Size(min = 2, message = "should contain at least {min} characters.")
    private String brand;
    @NotNull
    private long categoryId;
    private String description;
    private boolean isCancellable = false;
    private boolean isReturnable = false;
}
