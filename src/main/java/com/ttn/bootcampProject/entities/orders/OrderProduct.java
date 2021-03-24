package com.ttn.bootcampProject.entities.orders;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private int quantity;
    private double price;
    @Column(columnDefinition = "json")
    private String productVariationMetadata;
}
