package com.ttn.bootcampProject.entities.orders;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "order_product_id")
public class OrderStatus extends OrderProduct{

    private String fromStatus;
    private String toStatus;
    private String transitionNotesComments;
}
