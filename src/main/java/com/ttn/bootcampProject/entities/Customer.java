package com.ttn.bootcampProject.entities;

import javax.persistence.*;

@Entity
@PrimaryKeyJoinColumn(name = "user_id")
public class Customer extends User{

    private String contact;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
