package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.dtos.UpdatePasswordDto;
import com.ttn.bootcampProject.emailservices.MailService;
import com.ttn.bootcampProject.entities.ConfirmationToken;
import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.repos.ConfirmationTokenRepository;
import com.ttn.bootcampProject.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;


@Repository
public class ForgotPasswordService {

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MailService mailService;

    public ResponseEntity<String> forgotPassword(String email)
    {
        User user = userRepository.findByEmail(email);

        // checking if user exist
        if(user == null)
        {
            return new ResponseEntity("Account does not exist for the provided mail id.",HttpStatus.BAD_REQUEST);
        }

        // checking if user is active or not
        if(!user.isActive())
        {
            return new ResponseEntity("Account is de-activated account.",HttpStatus.BAD_REQUEST);
        }

        // creating and saving a token for the user fetched by mail id
        ConfirmationToken token = new ConfirmationToken(user);
        confirmationTokenRepository.save(token);

        // sending mail to the user for further process
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Reset account password!");
        mailMessage.setFrom("vardanbalyan97@gmail.com");
        mailMessage.setText("To reset your account password, please click here : "
                +"http://localhost:8080/reset-password?token="
                +token.getConfirmationToken());
        mailService.sendForgotPasswordMail(mailMessage);

        return new ResponseEntity("Please check your mail to reset password."
                ,HttpStatus.CREATED);
    }


    public ResponseEntity<String> resetPassword(UpdatePasswordDto updatePasswordDto
            , String forgotPasswordToken)
    {
        // getting the token to reset password which was created in forgot password api
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(forgotPasswordToken);
        // setting expiration time in millisecond for the token
        long expirationTime = 5*60000;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // checking if token exists
        if(token == null)
        {
            return new ResponseEntity("Invalid token",HttpStatus.NOT_FOUND);
        }

        User registeredUser = userRepository.findByUserId(token.getUser().getId());

        // checking if token expired or not
        if((token.getCreatedDate().getTime() + expirationTime) > System.currentTimeMillis())
        {
            // checking if new password and confirm password is same or not
            if(updatePasswordDto.getNewPassword().equals(updatePasswordDto.getConfirmPassword()))
            {
                registeredUser.setPassword(encoder.encode(updatePasswordDto.getNewPassword()));
                userRepository.save(registeredUser);
                // deleting the token from the database
                confirmationTokenRepository.deleteById(token.getTokenId());

                return new ResponseEntity("Password reset successful!!",HttpStatus.CREATED);
            }

            return new ResponseEntity("New password and confirm password should be same.",HttpStatus.BAD_REQUEST);
        }

        // deleting the token if token got expired
        confirmationTokenRepository.deleteById(token.getTokenId());
        return new ResponseEntity("Token expired!!",HttpStatus.BAD_REQUEST);

    }
}
