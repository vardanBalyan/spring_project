package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.config.AppUser;
import com.ttn.bootcampProject.config.GrantAuthorityImpl;
import com.ttn.bootcampProject.emailservices.MailService;
import com.ttn.bootcampProject.entities.*;
import com.ttn.bootcampProject.exceptions.UserNotFoundException;
import com.ttn.bootcampProject.dtos.GetAllCustomerInfoDto;
import com.ttn.bootcampProject.dtos.GetAllSellersInfoDto;
import com.ttn.bootcampProject.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
public class UserDaoService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserRoleRepository userRoleRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    SellerRepository sellerRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    MailService mailService;

    public AppUser loadUserByUsername(String email) {

        // getting user details
        User user = userRepository.findByEmail(email);
        // getting list of user roles for the user id from user_role table
        List<UserRole> roles = userRoleRepository.findRecordForUserId(user.getId());
        List<Long> roleIds = new ArrayList<>();
        Iterator<UserRole> userRoleIterator = roles.iterator();

        while (userRoleIterator.hasNext())
        {
            UserRole fetchedUserRole = userRoleIterator.next();
            // fetching all roles and adding to a list of role for the user
            roleIds.add(fetchedUserRole.getRole().getId());
        }

        List<GrantAuthorityImpl> authorities = new ArrayList<>();

        Iterator<Long> roleIdIterator = roleIds.iterator();

        while (roleIdIterator.hasNext())
        {
            // adding roles to the grant authority list
            authorities.add(new GrantAuthorityImpl(roleRepository.findAuthorityById(roleIdIterator.next())));
        }

        System.out.println(user);
        if (email != null) {
            return new AppUser(user.getEmail(), user.getPassword(), authorities);
        } else {
            throw new RuntimeException();
        }

    }

    public boolean checkUserIsActive(String email)
    {
        // getting user details for the email provided
        User user = userRepository.findByEmail(email);

        // checking if user exist
        if(user == null)
            throw new UserNotFoundException("no user found for the specified email");
        else
        {
            return user.isActive();
        }
    }

    public boolean checkUserIsDeleted(String email)
    {
        User user = userRepository.findByEmail(email);

        if(user == null)
            throw new UserNotFoundException("no user found for the specified email");
        else
        {
            return user.isDeleted();
        }

    }

    public List<GetAllCustomerInfoDto> getAllCustomers()
    {
        // getting all customer ids from customer tables
        List<Long> ids = customerRepository.fetchAllIdsOfCustomers();
        List<GetAllCustomerInfoDto> getAllCustomerInfoDtoList = new ArrayList<>();

        // adding the information to each customer to a list
        for (Long id: ids) {
            User user = userRepository.findByUserId(id);
            getAllCustomerInfoDtoList.add(new GetAllCustomerInfoDto(
                    user.getId(),
                    (user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName()),
                    user.getEmail(),
                    user.isActive()));
        }

        return getAllCustomerInfoDtoList;
    }

    public List<GetAllSellersInfoDto> getAllSellers()
    {
        // getting all ids of sellers from the seller table
        List<Long> ids = sellerRepository.fetchAllIdsOfSellers();
        List<GetAllSellersInfoDto> getAllSellersInfoDtoList = new ArrayList<>();
        List<Address> addresses;

        // adding info of each seller to a list
        for (Long id: ids) {
            User user = userRepository.findByUserId(id);
            addresses = addressRepository.findAddressByUserId(id);
            Seller seller = sellerRepository.findSellerByUserId(id);

            getAllSellersInfoDtoList.add(new GetAllSellersInfoDto(
                    user.getId(),
                    (user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName()),
                    user.getEmail(),
                    user.isActive(),seller.getCompanyName(),
                    seller.getCompanyContact(), addresses));
        }

        return getAllSellersInfoDtoList;
    }

    public ResponseEntity<String> activateSeller(long id)
    {
        // getting the seller for the provided id
        User user = userRepository.findByUserId(id);
        Seller seller = sellerRepository.findSellerByUserId(id);

        // checking if users exists
        if(user == null)
            return new ResponseEntity("User not found with particular id.",HttpStatus.NOT_FOUND);
        else
        {
            // checking if the id provided is of seller or not
            if(seller == null)
                return new ResponseEntity("No seller found with particular id.",HttpStatus.NOT_FOUND);
        }

        // checking if already active
        if(seller.isActive())
        {
            return new ResponseEntity("Seller is already active.",HttpStatus.OK);
        }

        // activating if not active
        user.setActive(true);
        userRepository.save(user);

        // send mail for activation notification
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(seller.getEmail());
        mailMessage.setSubject("Account status");
        mailMessage.setFrom("vardanbalyan97@gmail.com");
        mailMessage.setText("Your account is now active.");

        mailService.sendActivationMail(mailMessage);

        return new ResponseEntity("Seller is now active.",HttpStatus.ACCEPTED);
    }


    public ResponseEntity<String> deactivateSeller(long id)
    {
        // getting the seller for the provided id
        User user = userRepository.findByUserId(id);
        Seller seller = sellerRepository.findSellerByUserId(id);

        // checking if users exists
        if(user == null)
            return new ResponseEntity("User not found with particular id.",HttpStatus.NOT_FOUND);
        else
        {
            // checking if the id provided is of seller or not
            if(seller == null)
                return new ResponseEntity("No seller found with particular id.",HttpStatus.NOT_FOUND);
        }

        // checking if already de-active
        if(!seller.isActive())
        {
            return new ResponseEntity("Seller is already de-active.",HttpStatus.OK);
        }

        // deactivating if not de-active
        user.setActive(false);
        userRepository.save(user);
        // send mail for deactivation notification
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(seller.getEmail());
        mailMessage.setSubject("Account status");
        mailMessage.setFrom("vardanbalyan97@gmail.com");
        mailMessage.setText("Your account is now de-activated.");

        mailService.sendActivationMail(mailMessage);

        return new ResponseEntity("Seller is now de-active.",HttpStatus.ACCEPTED);
    }


    public ResponseEntity<String> activateCustomer(long id)
    {
        // getting the customer for the provided id
        User user = userRepository.findByUserId(id);
        Customer customer = customerRepository.findCustomerById(id);

        // checking if users exists
        if(user == null)
            return new ResponseEntity("User not found with particular id.",HttpStatus.NOT_FOUND);
        else
        {
            // checking if the id provided is of customer or not
            if(customer == null)
                return new ResponseEntity("No customer found with particular id.",HttpStatus.NOT_FOUND);
        }

        // checking if already active
        if(customer.isActive())
        {
            return new ResponseEntity("Customer is already active.",HttpStatus.OK);
        }

        // activating if not active
        user.setActive(true);
        userRepository.save(user);
        // send mail for activation notification
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(customer.getEmail());
        mailMessage.setSubject("Account status");
        mailMessage.setFrom("vardanbalyan97@gmail.com");
        mailMessage.setText("Your account is now active.");

        mailService.sendActivationMail(mailMessage);

        return new ResponseEntity("Customer is now active.",HttpStatus.ACCEPTED);
    }

    public ResponseEntity<String> deactivateCustomer(long id)
    {
        // getting the customer for the provided id
        User user = userRepository.findByUserId(id);
        Customer customer = customerRepository.findCustomerById(id);

        // checking if users exists
        if(user == null)
            return new ResponseEntity("User not found with particular id.",HttpStatus.NOT_FOUND);
        else
        {
            // checking if the id provided is of customer or not
            if(customer == null)
                return new ResponseEntity("No customer found with particular id.",HttpStatus.NOT_FOUND);
        }

        // checking if already de-active
        if(!customer.isActive())
        {
            return new ResponseEntity("Customer is already de-active.",HttpStatus.OK);
        }

        // deactivating if not de-active
        user.setActive(false);
        userRepository.save(user);
        // send mail for deactivation notification
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(customer.getEmail());
        mailMessage.setSubject("Account status");
        mailMessage.setFrom("vardanbalyan97@gmail.com");
        mailMessage.setText("Your account is now de-activated.");

        mailService.sendActivationMail(mailMessage);

        return new ResponseEntity("Customer is now de-active.",HttpStatus.ACCEPTED);
    }

    public List<User> giveAllUsers()
    {
        return userRepository.allUsersInfo();
    }
}
