package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.orders.OrderStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface OrderStatusRepository extends CrudRepository<OrderStatus, Long> {

    OrderStatus findById(long id);
}
