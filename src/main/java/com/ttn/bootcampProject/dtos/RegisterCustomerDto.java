package com.ttn.bootcampProject.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ttn.bootcampProject.entities.Address;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public class RegisterCustomerDto {
    @Email
    private String email;
    @Size(min = 3)
    private String firstName;
    private String middleName;
    @Size(min = 3)
    private String lastName;
    private String password;
    @Size(min = 10, max = 10)
    private String contact;
    private Set<Address> addresses;
}
