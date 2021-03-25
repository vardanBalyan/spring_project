package com.ttn.bootcampProject;

import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.entities.UserRole;
import com.ttn.bootcampProject.repos.UserRepository;
import com.ttn.bootcampProject.repos.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class UserDao {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserRoleRepository userRoleRepository;

    AppUser loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email);
        List<UserRole> roles = userRoleRepository.findRecordForUserId(user.getId());


        System.out.println(user);
        if (email != null) {
            return new AppUser(user.getEmail(), user.getPassword(), Arrays.asList(new GrantAuthorityImpl(user.getRole())));
        } else {
            throw new RuntimeException();
        }

    }
}
