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
        User user = userRepository.findByEmail(email);
        //System.out.println("id is >>>>>>>>>>>>>"+user.getId());
        List<UserRole> roles = userRoleRepository.findRecordForUserId(user.getId());
        List<Long> roleIds = new ArrayList<>();
        Iterator<UserRole> userRoleIterator = roles.iterator();

        while (userRoleIterator.hasNext())
        {
            UserRole fetchedUserRole = userRoleIterator.next();
            roleIds.add(fetchedUserRole.getRole().getId());
        }

        List<GrantAuthorityImpl> authorities = new ArrayList<>();

        Iterator<Long> roleIdIterator = roleIds.iterator();

        while (roleIdIterator.hasNext())
        {
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
        User user = userRepository.findByEmail(email);

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
        List<Long> ids = customerRepository.fetchAllIdsOfCustomers();
        List<GetAllCustomerInfoDto> getAllCustomerInfoDtoList = new ArrayList<>();

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
        List<Long> ids = sellerRepository.fetchAllIdsOfSellers();
        List<GetAllSellersInfoDto> getAllSellersInfoDtoList = new ArrayList<>();
        List<Address> addresses;

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
        User user = userRepository.findByUserId(id);
        Seller seller = sellerRepository.findSellerByUserId(id);

        if(user == null)
            return new ResponseEntity("User not found with particular id.",HttpStatus.NOT_FOUND);
        else
        {
            if(seller == null)
                return new ResponseEntity("No seller found with particular id.",HttpStatus.NOT_FOUND);
        }

        if(seller.isActive())
        {
            return new ResponseEntity("Seller is already active.",HttpStatus.OK);
        }

        user.setActive(true);
        userRepository.save(user);

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
        User user = userRepository.findByUserId(id);
        Seller seller = sellerRepository.findSellerByUserId(id);

        if(user == null)
            return new ResponseEntity("User not found with particular id.",HttpStatus.NOT_FOUND);
        else
        {
            if(seller == null)
                return new ResponseEntity("No seller found with particular id.",HttpStatus.NOT_FOUND);
        }

        if(!seller.isActive())
        {
            return new ResponseEntity("Seller is already de-active.",HttpStatus.OK);
        }

        user.setActive(false);
        userRepository.save(user);
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
        User user = userRepository.findByUserId(id);
        Customer customer = customerRepository.findCustomerById(id);

        if(user == null)
            return new ResponseEntity("User not found with particular id.",HttpStatus.NOT_FOUND);
        else
        {
            if(customer == null)
                return new ResponseEntity("No customer found with particular id.",HttpStatus.NOT_FOUND);
        }

        if(customer.isActive())
        {
            return new ResponseEntity("Customer is already active.",HttpStatus.OK);
        }

        user.setActive(true);
        userRepository.save(user);
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
        User user = userRepository.findByUserId(id);
        Customer customer = customerRepository.findCustomerById(id);

        if(user == null)
            return new ResponseEntity("User not found with particular id.",HttpStatus.NOT_FOUND);
        else
        {
            if(customer == null)
                return new ResponseEntity("No customer found with particular id.",HttpStatus.NOT_FOUND);
        }

        if(!customer.isActive())
        {
            return new ResponseEntity("Customer is already de-active.",HttpStatus.OK);
        }

        user.setActive(false);
        userRepository.save(user);
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
