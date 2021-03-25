package com.ttn.bootcampProject;

import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;

@Repository
public class UserDao {

    @Autowired
    UserRepository userRepository;

    AppUser loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email);
        System.out.println(user);
        if (email != null) {

            return new AppUser(user.getEmail(), user.getPassword(), Arrays.asList(new GrantAuthorityImpl(user.getRole())));
        } else {
            throw new RuntimeException();
        }

    }
}
