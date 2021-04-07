package com.ttn.bootcampProject.dtos;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Map;

@Getter
@Setter
public class AddProductVariationDto {

    @NotNull
    private long productId;
    @Positive
    private Double price;
    @Positive
    private long quantity;
    @NotNull
    private Map<String,String> metadata;
    private String primaryImageName;
}
