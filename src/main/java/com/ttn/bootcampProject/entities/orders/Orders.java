package com.ttn.bootcampProject.entities.orders;

import com.ttn.bootcampProject.entities.Customer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Double amountPaid;
    private Date dateCreated;
    private String paymentMethod;
    private String customerAddressCity;
    private String customerAddressState;
    private String customerAddressCountry;
    private String customerAddressAddressLine;
    private String customerAddressZipCode;
    private String customerAddressLabel;
}
