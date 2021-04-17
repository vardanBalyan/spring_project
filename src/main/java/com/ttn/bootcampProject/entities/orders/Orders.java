package com.ttn.bootcampProject.entities.orders;

import com.ttn.bootcampProject.entities.Customer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private Double amountPaid;
    private Date dateCreated;
    private String paymentMethod;
    private String customerAddressCity;
    private String customerAddressState;
    private String customerAddressCountry;
    private String customerAddressAddressLine;
    private String customerAddressZipCode;
    private String customerAddressLabel;
    @ManyToOne
    @JoinColumn(name = "customer_user_id")
    private Customer customer;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orders")
    private List<OrderProduct> orderProductList;

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
