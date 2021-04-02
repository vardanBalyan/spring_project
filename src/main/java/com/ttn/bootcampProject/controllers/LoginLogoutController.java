package com.ttn.bootcampProject.controllers;

import com.ttn.bootcampProject.services.UserDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LoginLogoutController {

    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private UserDaoService userService;

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/admin/home")
    public String adminHome(){
        return "Admin home";
    }

    @GetMapping("/customer/home/{email}")
    public String customerHome(@PathVariable String email)
    {
        if(!userService.checkUserIsDeleted(email))
        {
            if(userService.checkUserIsActive(email))
            {
                return "Customer home";
            }
            return "User is not active";
        }
        return "User doesn't exist";
    }

    @GetMapping("/seller/home/{email}")
    public String sellerHome(@PathVariable String email)
    {
        if(!userService.checkUserIsDeleted(email))
        {
            if(userService.checkUserIsActive(email))
            {
                return "Customer home";
            }
            return "User is not active";
        }
        return "User doesn't exist";
    }

    @GetMapping("/doLogout")
    public String logout(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            String tokenValue = authHeader.replace("Bearer", "").trim();
            OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
            tokenStore.removeAccessToken(accessToken);
        }
        return "Logged out successfully";
    }
}
