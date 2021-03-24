package com.ttn.bootcampProject.entities.products;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class ProductVariation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Double price;
    private long quantityAvailable;
    @Column(columnDefinition = "json")
    private String metadata;
    private String primaryImageName;
    private boolean isActive;
}
