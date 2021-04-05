package com.ttn.bootcampProject.dtos;

import com.ttn.bootcampProject.constraints.ValidPassword;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UpdatePasswordDto {

    @Pattern(regexp="((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,15})"
            ,message="Password must contain 8-15 Characters with atleast 1 Lower case, " +
            "1 Upper case, 1 Special Character, 1 Number")
    private String newPassword;
    @Pattern(regexp="((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,15})"
            ,message="Password must contain 8-15 Characters with atleast 1 Lower case, " +
            "1 Upper case, 1 Special Character, 1 Number")
    private String confirmPassword;
}
