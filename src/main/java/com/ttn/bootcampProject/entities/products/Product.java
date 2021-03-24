package com.ttn.bootcampProject.entities.products;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private boolean isCancellable;
    private String brand;
    private boolean isReturnable;
    private boolean isActive;
    @OneToMany(cascade = CascadeType.ALL)
    private List<ProductReview> productReviews;

    public void addReviews(ProductReview review)
    {
        if(review!=null)
        {
            if(productReviews == null)
            {
                productReviews = new ArrayList<>();
            }
            productReviews.add(review);
            review.setProduct(this);
        }
    }
}
