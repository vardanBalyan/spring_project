package com.ttn.bootcampProject.entities.products;

import com.ttn.bootcampProject.entities.Customer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class ProductReview {

    @EmbeddedId
    private ProductReviewId id;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @MapsId("customerUserId")
    @JoinColumn(name = "customer_user_id")
    private Customer customer;

    private String review;
    private String rating;
}
