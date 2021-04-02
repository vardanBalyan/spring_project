package com.ttn.bootcampProject.entities;

import com.ttn.bootcampProject.entities.products.Product;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
public class Seller extends User{

    private String gst;
    private String companyContact;
    private String companyName;
    private String image;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "seller_user_id")
    private List<Product> productList;

    public void addProducts(Product product)
    {
        if(product != null)
        {
            if(productList == null)
            {
                productList = new ArrayList<>();
            }
            productList.add(product);
        }
    }
}
