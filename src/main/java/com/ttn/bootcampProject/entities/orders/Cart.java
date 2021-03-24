package com.ttn.bootcampProject.entities.orders;

import com.ttn.bootcampProject.entities.Customer;
import com.ttn.bootcampProject.entities.products.ProductVariation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Cart {

    @EmbeddedId
    private CartId cartId;

    @ManyToOne
    @MapsId("customerUserId")
    @JoinColumn(name = "customer_user_id")
    private Customer customer;

    @ManyToOne
    @MapsId("productVariationId")
    @JoinColumn(name = "product_variation_id")
    private ProductVariation productVariation;

    private int quantity;
}
