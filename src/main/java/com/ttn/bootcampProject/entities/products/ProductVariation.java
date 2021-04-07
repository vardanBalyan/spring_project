package com.ttn.bootcampProject.entities.products;


import com.ttn.bootcampProject.entities.orders.Cart;
import com.ttn.bootcampProject.entities.orders.OrderProduct;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@TypeDef(name = "jsonb", typeClass = JsonStringType.class)
public class ProductVariation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private Double price;
    private long quantityAvailable;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String,String> metadata;
    private String primaryImageName;
    private boolean isActive;

    @OneToMany
    @JoinColumn(name = "product_variation_id")
    private List<OrderProduct> orderProductList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productVariation")
    private List<Cart> cartList;

    public ProductVariation() {
        this.isActive = true;
    }

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
