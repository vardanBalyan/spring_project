package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.config.AppUser;
import com.ttn.bootcampProject.config.GrantAuthorityImpl;
import com.ttn.bootcampProject.entities.*;
import com.ttn.bootcampProject.exceptions.UserNotFoundException;
import com.ttn.bootcampProject.dtos.CustomerInfoDto;
import com.ttn.bootcampProject.dtos.SellersInfoDto;
import com.ttn.bootcampProject.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class UserDao {

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

    public List<CustomerInfoDto> getAllCustomers()
    {
        List<Long> ids = customerRepository.fetchAllIdsOfCustomers();
        List<CustomerInfoDto> customerInfoDtoList = new ArrayList<>();

        for (Long id: ids) {
            User user = userRepository.findByUserId(id);
            customerInfoDtoList.add(new CustomerInfoDto(
                    user.getId(),
                    (user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName()),
                    user.getEmail(),
                    user.isActive()));
        }

        return customerInfoDtoList;
    }

    public List<SellersInfoDto> getAllSellers()
    {
        List<Long> ids = sellerRepository.fetchAllIdsOfSellers();
        List<SellersInfoDto> sellersInfoDtoList = new ArrayList<>();
        List<Address> addresses;

        for (Long id: ids) {
            User user = userRepository.findByUserId(id);
            addresses = addressRepository.findByAddressId(id);
            Seller seller = sellerRepository.findSellerById(id);

            sellersInfoDtoList.add(new SellersInfoDto(
                    user.getId(),
                    (user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName()),
                    user.getEmail(),
                    user.isActive(),seller.getCompanyName(),
                    seller.getCompanyContact(), addresses));
        }

        return sellersInfoDtoList;
    }

    public Seller activateSeller(long id)
    {
        User user = userRepository.findByUserId(id);
        Seller seller = sellerRepository.findSellerById(id);

        if(user == null)
            return null;
        else
        {
            if(seller == null)
                return null;
        }

        user.setActive(true);
        userRepository.save(user);

        return seller;
    }


    public String deactivateSeller(long id)
    {
        User user = userRepository.findByUserId(id);
        Seller seller = sellerRepository.findSellerById(id);

        if(user == null)
            return "No user exist with the provided id.";
        else
        {
            if(seller == null)
                return "The provided user id is not seller.";
        }

        if(user.isActive())
        {
            user.setActive(false);
            userRepository.save(user);
            return "Seller is now de-active.";
        }
        return "Seller is already deactivated.";
    }


    public Customer activateCustomer(long id)
    {
        User user = userRepository.findByUserId(id);
        Customer customer = customerRepository.findCustomerById(id);

        if(user == null)
            return null;
        else
        {
            if(customer == null)
                return null;
        }

        user.setActive(true);
        userRepository.save(user);
        return customer;

    }

    public String deactivateCustomer(long id)
    {
        User user = userRepository.findByUserId(id);
        Customer customer = customerRepository.findCustomerById(id);

        if(user == null)
            return "No user exist with the provided id.";
        else
        {
            if(customer == null)
                return "The provided user id is not customer.";
        }

        if(user.isActive())
        {
            user.setActive(false);
            userRepository.save(user);
            return "Customer is now de-active.";
        }
        return "Customer is already deactivated.";
    }

    public List<User> giveAllUsers()
    {
        return userRepository.allUsersInfo();
    }
}
