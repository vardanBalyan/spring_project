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
    @Email(message = "must be a valid email")
    private String email;
    @Size(min = 3)
    private String firstName;
    private String middleName;
    @Size(min = 3)
    private String lastName;
    private String password;
    private Set<Address> addresses;
    @Size(min = 15, max = 15)
    private String gst;
    @Size(min = 10, max = 10)
    private String companyContact;
    @Size(min = 4)
    private String companyName;
}
