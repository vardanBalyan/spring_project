package com.ttn.bootcampProject.controllers;

import com.ttn.bootcampProject.dtos.UpdatePasswordDto;
import com.ttn.bootcampProject.services.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ForgotPasswordController {

    @Autowired
    ForgotPasswordService passwordService;

    @PostMapping(path = "/forgot-password/{email}")
    public ResponseEntity<String> forgotPassword(@PathVariable String email)
    {
        return passwordService.forgotPassword(email);
    }

    @PatchMapping(path = "/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token")String forgotPasswordToken
            ,@RequestBody UpdatePasswordDto updatePasswordDto)
    {
        return passwordService.resetPassword(updatePasswordDto, forgotPasswordToken);
    }
}
