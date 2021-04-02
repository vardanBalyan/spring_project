package com.ttn.bootcampProject.dtos;

import com.ttn.bootcampProject.constraints.ValidPassword;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
public class UpdatePasswordDto {

    @ValidPassword
    private String newPassword;
    @ValidPassword
    private String confirmPassword;
}
