package com.ttn.bootcampProject.helpingclasses;

import com.ttn.bootcampProject.entities.Address;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SellersInfo {
    private long id;
    private String fullName;
    private String email;
    private boolean isActive;
    private String companyName;
    private String companyContact;
    private List<Address> addresses;

    public SellersInfo(long id, String fullName, String email, boolean isActive, String companyName, String companyContact, List<Address> addresses) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.isActive = isActive;
        this.companyName = companyName;
        this.companyContact = companyContact;
        this.addresses = addresses;
    }
}
