package com.ttn.bootcampProject.entities.products;


import com.ttn.bootcampProject.entities.orders.Cart;
import com.ttn.bootcampProject.entities.orders.OrderProduct;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class ProductVariation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private Double price;
    private long quantityAvailable;
    @Column(columnDefinition = "json")
    private String metadata;
    private String primaryImageName;
    private boolean isActive;

    @OneToMany
    @JoinColumn(name = "product_variation_id")
    private List<OrderProduct> orderProductList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productVariation")
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

    public void addOrderProduct(OrderProduct orderProduct)
    {
        if(orderProduct!=null)
        {
            if(orderProductList == null)
            {
                orderProductList = new ArrayList<>();
            }
            orderProductList.add(orderProduct);
        }
    }
}
