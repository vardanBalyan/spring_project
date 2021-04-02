package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.dtos.ForgotPasswordDto;
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
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
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
        ConfirmationToken token = new ConfirmationToken(user);
        confirmationTokenRepository.save(token);

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

    public ResponseEntity<String> resetPassword(ForgotPasswordDto forgotPasswordDto)
    {
        User registeredUser = userRepository.findByEmail(forgotPasswordDto.getEmail());
        ConfirmationToken token = confirmationTokenRepository.findByUserId(registeredUser.getId());
        long expirationTime = 120000;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if((token.getCreatedDate().getTime()+expirationTime)>System.currentTimeMillis())
        {
            if(forgotPasswordDto.getNewPassword().equals(forgotPasswordDto.getConfirmPassword()))
            {
                System.out.println(">>>>>>>>>>>>>>"+token.getCreatedDate().getTime());
                registeredUser.setPassword(forgotPasswordDto.getNewPassword());
                userRepository.save(registeredUser);
                confirmationTokenRepository.deleteById(token.getTokenId());

                return new ResponseEntity("Password reset successful!!",HttpStatus.CREATED);
            }

            return new ResponseEntity("New password and confirm password should be same.",HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity("Token expired!!",HttpStatus.BAD_REQUEST);

    }
}
