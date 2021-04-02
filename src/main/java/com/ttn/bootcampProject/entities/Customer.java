package com.ttn.bootcampProject.entities;

import com.ttn.bootcampProject.entities.orders.Cart;
import com.ttn.bootcampProject.entities.orders.Orders;
import com.ttn.bootcampProject.entities.products.ProductReview;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
public class Customer extends User{

    private String contact;
    private String image;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_user_id")
    private Set<Orders> ordersSet;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    private List<ProductReview> productReviews;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    private List<Cart> cartList;

    public void addCartItems(Cart cart)
    {
        if(cart!=null)
        {
            if(cartList == null)
            {
                cartList = new ArrayList<>();
            }
            cartList.add(cart);
        }
    }


    public void addOrders(Orders orders)
    {
        if(orders != null)
        {
            if(ordersSet == null)
            {
                ordersSet = new HashSet<>();
            }
            ordersSet.add(orders);
        }
    }

    public void addReviews(ProductReview review)
    {
        if(review!=null)
        {
            if(productReviews == null)
            {
                productReviews = new ArrayList<>();
            }
            productReviews.add(review);
            review.setCustomer(this);
        }
    }
}
