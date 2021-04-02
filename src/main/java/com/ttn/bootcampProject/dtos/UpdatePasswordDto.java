package com.ttn.bootcampProject.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
public class UpdatePasswordDto {

    private String newPassword;
    private String confirmPassword;
}
