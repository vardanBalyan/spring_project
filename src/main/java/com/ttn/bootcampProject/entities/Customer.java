package com.ttn.bootcampProject.entities;

import com.ttn.bootcampProject.entities.orders.Order;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
public class Customer extends User{

    private String contact;
    @OneToMany
    @JoinColumn(name = "customer_user_id")
    private Set<Order> orders;
}
