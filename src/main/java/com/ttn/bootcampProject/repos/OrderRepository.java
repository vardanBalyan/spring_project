package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.orders.Orders;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Orders, Integer> {
}
