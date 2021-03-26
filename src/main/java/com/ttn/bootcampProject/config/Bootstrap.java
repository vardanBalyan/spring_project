package com.ttn.bootcampProject.config;


import com.ttn.bootcampProject.entities.*;
import com.ttn.bootcampProject.repos.RoleRepository;
import com.ttn.bootcampProject.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class Bootstrap implements ApplicationRunner {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Role adminRole = new Role();
        adminRole.setAuthority("ROLE_ADMIN");
        Role sellerRole = new Role();
        sellerRole.setAuthority("ROLE_SELLER");
        Role customerRole = new Role();
        customerRole.setAuthority("ROLE_CUSTOMER");

        if(userRepository.count()<1){
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            Admin admin = new Admin();
            admin.setFirstName("admin");
            admin.setDeleted(false);
            admin.setEmail("admin");
            admin.setPassword(passwordEncoder.encode("pass"));
            admin.setActive(true);
            admin.setContact("4666762");

            UserRole roleOfAdmin = new UserRole(admin,adminRole);
            admin.addRoles(roleOfAdmin);

            Customer customer = new Customer();
            customer.setFirstName("Vardan");
            customer.setLastName("Balyan");
            customer.setContact("5735677");
            customer.setDeleted(false);
            customer.setEmail("vardanbalyan@gmail.com");
            customer.setPassword(passwordEncoder.encode("pass"));
            customer.setActive(true);

            Address address = new Address();
            address.setZipCode("110099");
            address.setLabel("home");
            address.setCountry("india");
            address.setState("delhi");
            address.setCity("new delhi");
            address.setAddressLine("212 Sadar bazar");

            Set<Address> addresses = new HashSet<>();
            addresses.add(address);
            customer.setAddresses(addresses);

            UserRole roleOfCustomer = new UserRole(customer, customerRole);
            customer.addRoles(roleOfCustomer);

            userRepository.save(customer);
            userRepository.save(admin);
            System.out.println("Total users saved::"+userRepository.count());
        }
        roleRepository.save(adminRole);
        roleRepository.save(sellerRole);
        roleRepository.save(customerRole);
    }
}
