package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.dtos.CustomerProfileDto;
import com.ttn.bootcampProject.dtos.UpdatePasswordDto;
import com.ttn.bootcampProject.entities.Address;
import com.ttn.bootcampProject.entities.Customer;
import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.repos.AddressRepository;
import com.ttn.bootcampProject.repos.CustomerRepository;
import com.ttn.bootcampProject.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerDaoService {

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AddressRepository addressRepository;

    public CustomerProfileDto getProfile(String email)
    {
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());
        CustomerProfileDto customerProfile = new CustomerProfileDto();
        customerProfile.setId(customer.getId());
        customerProfile.setFirstName(customer.getFirstName());
        customerProfile.setLastName(customer.getLastName());
        customerProfile.setActive(customer.isActive());
        customerProfile.setContact(customer.getContact());
        customerProfile.setImage(customer.getImage());

        return customerProfile;
    }

    public List<Address> getAddresses(String email)
    {
        User user = userRepository.findByEmail(email);
        return user.getAddresses();
    }

    public ResponseEntity<String> updateProfile(CustomerProfileDto customerProfileDto, String email)
    {
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());
        customer.setFirstName(customerProfileDto.getFirstName());
        customer.setLastName(customerProfileDto.getLastName());
        customer.setContact(customerProfileDto.getContact());
        customer.setImage(customerProfileDto.getImage());

        customerRepository.save(customer);
        return new ResponseEntity("Profile updated successfully.",HttpStatus.ACCEPTED);
    }

    public ResponseEntity<String> updatePassword(UpdatePasswordDto updatePasswordDto, String email)
    {
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(">>>>>>>>>>>>>>>>>>>"+encoder.matches(updatePasswordDto.getNewPassword(), customer.getPassword()));

        if(updatePasswordDto.getNewPassword().equals(updatePasswordDto.getConfirmPassword()))
        {
            if(encoder.matches(updatePasswordDto.getNewPassword(), customer.getPassword()))
            {
                return new ResponseEntity("Current password and new password should be different."
                        ,HttpStatus.BAD_REQUEST);
            }

            customer.setPassword(encoder.encode(updatePasswordDto.getNewPassword()));
            customerRepository.save(customer);
            return new ResponseEntity("Password updated successfully.",HttpStatus.ACCEPTED);
        }
        return new ResponseEntity("New password and confirm password should be same.",HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> addNewAddress(Address address, String email)
    {
        User user = userRepository.findByEmail(email);
        Customer customer = customerRepository.findCustomerById(user.getId());
        List<Address> addressList = customer.getAddresses();

        Address newAddress = new Address();
        newAddress.setAddressLine(address.getAddressLine());
        newAddress.setCity(address.getCity());
        newAddress.setState(address.getState());
        newAddress.setCountry(address.getCountry());
        newAddress.setLabel(address.getLabel());
        newAddress.setZipCode(address.getZipCode());

        addressList.add(newAddress);
        customer.setAddresses(addressList);
        customerRepository.save(customer);

        return new ResponseEntity("New address added successfully.",HttpStatus.CREATED);
    }


    public ResponseEntity<String> updateAnAddress(Address address, long id, String email)
    {
        User user = userRepository.findByEmail(email);
        List<Long> addressIds = addressRepository.findAddressIdsForUserId(user.getId());

        if(addressIds.contains(id))
        {
            Address updatedAddress = addressRepository.findById(id);
            updatedAddress.setAddressLine(address.getAddressLine());
            updatedAddress.setLabel(address.getLabel());
            updatedAddress.setCountry(address.getCountry());
            updatedAddress.setState(address.getState());
            updatedAddress.setCity(address.getCity());
            updatedAddress.setZipCode(address.getZipCode());

            addressRepository.save(updatedAddress);
            return new ResponseEntity("Address updated successfully.",HttpStatus.CREATED);
        }
        return new ResponseEntity("No address found with particular id.",HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<String> deleteAnAddress(long id, String email)
    {
        User user = userRepository.findByEmail(email);
        List<Long> addressIds = addressRepository.findAddressIdsForUserId(user.getId());

        if(addressIds.contains(id))
        {
            addressRepository.deleteById(id);
            return new ResponseEntity("Address deleted successfully.",HttpStatus.CREATED);
        }
        return new ResponseEntity("No address found with particular id.",HttpStatus.NOT_FOUND);
    }
}
