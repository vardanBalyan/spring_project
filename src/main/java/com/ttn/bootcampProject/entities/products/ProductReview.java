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
    private ProductReviewId id = new ProductReviewId();

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

    public ProductReview(Product product, Customer customer, String review, String rating) {
        this.product = product;
        this.customer = customer;
        this.review = review;
        this.rating = rating;
    }

    public ProductReview() {
    }
}
