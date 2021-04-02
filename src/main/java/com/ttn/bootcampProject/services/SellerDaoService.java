package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.dtos.SellerProfileDto;
import com.ttn.bootcampProject.dtos.UpdatePasswordDto;
import com.ttn.bootcampProject.entities.Address;
import com.ttn.bootcampProject.entities.Seller;
import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.repos.AddressRepository;
import com.ttn.bootcampProject.repos.SellerRepository;
import com.ttn.bootcampProject.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SellerDaoService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    SellerRepository sellerRepository;
    @Autowired
    AddressRepository addressRepository;

    public SellerProfileDto getProfile(String email)
    {
        User user = userRepository.findByEmail(email);
        Seller seller = sellerRepository.findSellerByUserId(user.getId());

        SellerProfileDto sellerProfile = new SellerProfileDto();

        sellerProfile.setActive(seller.isActive());
        sellerProfile.setAddress(seller.getAddresses().get(0));
        sellerProfile.setCompanyContact(seller.getCompanyContact());
        sellerProfile.setCompanyName(seller.getCompanyName());
        sellerProfile.setGst(seller.getGst());
        sellerProfile.setFirstName(seller.getFirstName());
        sellerProfile.setId(seller.getId());
        sellerProfile.setImage(seller.getImage());
        sellerProfile.setLastName(seller.getLastName());

        return  sellerProfile;
    }

    public ResponseEntity<String> updateProfile(SellerProfileDto sellerProfileDto, String email)
    {
        User user = userRepository.findByEmail(email);
        Seller seller = sellerRepository.findSellerByUserId(user.getId());

        seller.setFirstName(sellerProfileDto.getFirstName());
        seller.setLastName(sellerProfileDto.getLastName());
        seller.setCompanyContact(sellerProfileDto.getCompanyContact());
        seller.setGst(sellerProfileDto.getGst());
        seller.setCompanyName(sellerProfileDto.getCompanyName());
        seller.setImage(sellerProfileDto.getImage());

        sellerRepository.save(seller);
        return new ResponseEntity("Profile updated successfully.", HttpStatus.ACCEPTED);
    }

    public ResponseEntity<String> updatePassword(UpdatePasswordDto updatePasswordDto, String email)
    {
        User user = userRepository.findByEmail(email);
        Seller seller = sellerRepository.findSellerByUserId(user.getId());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(updatePasswordDto.getNewPassword().equals(updatePasswordDto.getConfirmPassword()))
        {
            if(encoder.matches(updatePasswordDto.getNewPassword(), seller.getPassword()))
            {
                return new ResponseEntity("Current password and new password should be different."
                        ,HttpStatus.BAD_REQUEST);
            }

            seller.setPassword(encoder.encode(updatePasswordDto.getNewPassword()));
            sellerRepository.save(seller);
            return new ResponseEntity("Password updated successfully.",HttpStatus.ACCEPTED);
        }
        return new ResponseEntity("New password and confirm password should be same.",HttpStatus.BAD_REQUEST);
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
}
