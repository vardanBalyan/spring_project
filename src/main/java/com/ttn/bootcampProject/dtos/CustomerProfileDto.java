package com.ttn.bootcampProject.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class CustomerProfileDto {

    private long id;
    @Size(min = 3, message = "should contain at least {min} characters")
    private String firstName;
    @Size(min = 3, message = "should contain at least {min} characters")
    private String lastName;
    private boolean isActive;
    @Size(min = 10, max = 10, message = "should contain at least {min} digits only")
    private String contact;
    @Size(min = 3, message = "should contain at least {min} characters")
    private String image;
}
