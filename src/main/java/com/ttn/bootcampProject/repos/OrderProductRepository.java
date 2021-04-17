package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.orders.OrderProduct;
import org.springframework.data.repository.CrudRepository;

public interface OrderProductRepository extends CrudRepository<OrderProduct, Long> {
}
