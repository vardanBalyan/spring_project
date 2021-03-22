package com.ttn.bootcampProject.entities;

import com.ttn.bootcampProject.entities.products.Product;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "user_id")
public class Seller extends User{

    private String gst;
    private String companyContact;
    private String companyName;


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "seller_user_id")
    private List<Product> productList;

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public String getGst() {
        return gst;
    }


    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getCompanyContact() {
        return companyContact;
    }

    public void setCompanyContact(String companyContact) {
        this.companyContact = companyContact;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

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
