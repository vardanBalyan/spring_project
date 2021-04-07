package com.ttn.bootcampProject.dtos;

import com.ttn.bootcampProject.entities.products.categories.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisplayProductDto {

    private long id;
    private String name;
    private String description;
    private boolean isCancellable;
    private String brand;
    private boolean isReturnable;
    private boolean isActive;
    private long categoryId;
    private String categoryName;
}
