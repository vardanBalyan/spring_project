package com.ttn.bootcampProject.dtos;

import com.ttn.bootcampProject.entities.Address;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public class RegisterSellerDto {
    @Email(message = "must be a well-formed email")
    private String email;
    @Size(min = 3, message = "should contain at least {min} characters")
    private String firstName;
    private String middleName;
    @Size(min = 3, message = "should contain at least {min} characters")
    private String lastName;
    private String password;
    private Address address;
    @Size(min = 15, max = 15, message = "should contain valid gst number with 15 characters")
    private String gst;
    @Size(min = 10, max = 10, message = "should contain at least {min} digits only")
    private String companyContact;
    @Size(min = 4, message = "should contain at least {min} characters")
    private String companyName;
    private String confirmPassword;
}
