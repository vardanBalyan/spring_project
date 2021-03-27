package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.config.AppUser;
import com.ttn.bootcampProject.config.GrantAuthorityImpl;
import com.ttn.bootcampProject.entities.Address;
import com.ttn.bootcampProject.entities.Seller;
import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.entities.UserRole;
import com.ttn.bootcampProject.exceptions.UserNotFoundException;
import com.ttn.bootcampProject.helpingclasses.CustomerInfo;
import com.ttn.bootcampProject.helpingclasses.SellersInfo;
import com.ttn.bootcampProject.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
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

    public List<CustomerInfo> getAllCustomers()
    {
        List<Long> ids = customerRepository.fetchAllIdsOfCustomers();
        List<CustomerInfo> customerInfoList = new ArrayList<>();

        for (Long id: ids) {
            User user = userRepository.findByUserId(id);
            customerInfoList.add(new CustomerInfo(
                    user.getId(),
                    (user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName()),
                    user.getEmail(),
                    user.isActive()));
        }

        return customerInfoList;
    }

    public List<SellersInfo> getAllSellers()
    {
        List<Long> ids = sellerRepository.fetchAllIdsOfSellers();
        List<SellersInfo> sellersInfoList = new ArrayList<>();
        List<Address> addresses;

        for (Long id: ids) {
            User user = userRepository.findByUserId(id);
            addresses = addressRepository.findByAddressId(id);
            Seller seller = sellerRepository.findSellerById(id);

            sellersInfoList.add(new SellersInfo(
                    user.getId(),
                    (user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName()),
                    user.getEmail(),
                    user.isActive(),seller.getCompanyName(),
                    seller.getCompanyContact(), addresses));
        }

        return sellersInfoList;
    }
}
