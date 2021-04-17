package com.ttn.bootcampProject.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String city;
    private String state;
    private String country;
    private String addressLine;
    private String zipCode;
    private String label;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
