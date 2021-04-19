package com.ttn.bootcampProject.entities.orders;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "order_product_id")
public class OrderStatus extends OrderProduct{

    public enum Status{
        ORDER_PLACED, CANCELLED, ORDER_REJECTED, ORDER_CONFIRMED, ORDER_SHIPPED, DELIVERED, RETURN_REQUESTED,
        RETURN_REJECTED, RETURN_APPROVED, PICK_UP_INITIATED, PICK_UP_COMPLETED, REFUND_INITIATED , REFUND_COMPLETED, CLOSED
    }

    @Enumerated(EnumType.STRING)
    private Status fromStatus;
    @Enumerated(EnumType.STRING)
    private Status toStatus;
    private String transitionNotesComments;



}
