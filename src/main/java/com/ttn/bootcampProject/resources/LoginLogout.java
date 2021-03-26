package com.ttn.bootcampProject.resources;

import com.ttn.bootcampProject.exceptions.UserNotFoundException;
import com.ttn.bootcampProject.repos.UserRepository;
import com.ttn.bootcampProject.services.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LoginLogout {

    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private UserDao userService;

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
        if(userService.isCustomerActive(email))
            return "Customer home";
        else
            return "not activated";
            //throw new UserNotFoundException("user is not activated");
    }

    @GetMapping("/seller/home/{email}")
    public String sellerHome(@PathVariable String email)
    {
        if(userService.isCustomerActive(email))
            return "Seller home";
        else
            throw new UserNotFoundException("user is not activated");
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
