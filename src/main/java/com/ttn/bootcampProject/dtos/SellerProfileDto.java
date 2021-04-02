package com.ttn.bootcampProject.dtos;

import com.ttn.bootcampProject.entities.Address;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class SellerProfileDto {

    private long id;
    @Size(min = 3, message = "should contain at least {min} characters")
    private String firstName;
    @Size(min = 3, message = "should contain at least {min} characters")
    private String lastName;
    private boolean isActive;
    @Size(min = 15, max = 15, message = "should contain valid gst number with 15 characters")
    private String gst;
    @Size(min = 10, max = 10, message = "should contain at least {min} digits only")
    private String companyContact;
    @Size(min = 4, message = "should contain at least {min} characters")
    private String companyName;
    @Size(min = 3, message = "should contain at least {min} characters")
    private String image;
    private Address address;
}
