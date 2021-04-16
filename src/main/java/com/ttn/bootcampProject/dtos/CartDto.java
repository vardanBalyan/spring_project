package com.ttn.bootcampProject.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CartDto {

    @NotNull
    private Long variationId;
    @NotNull
    private Integer quantity;
}
