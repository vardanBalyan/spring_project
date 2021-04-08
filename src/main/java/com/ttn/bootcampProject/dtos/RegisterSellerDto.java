package com.ttn.bootcampProject.dtos;

import com.ttn.bootcampProject.constraints.ValidPassword;
import com.ttn.bootcampProject.entities.Address;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
    @NotNull
    @Pattern(regexp="((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,15})"
            ,message="Password must contain 8-15 Characters with atleast 1 Lower case, " +
            "1 Upper case, 1 Special Character, 1 Number")
    private String password;
    private Address address;
    @NotNull
   // @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$"
     //       ,message="GST number must be valid")
    private String gst;
    @Size(min = 10, max = 10, message = "should contain at least {min} digits only")
    private String companyContact;
    @Size(min = 4, message = "should contain at least {min} characters")
    private String companyName;
    private String confirmPassword;
}
