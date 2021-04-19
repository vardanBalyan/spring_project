package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.orders.Orders;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdersRepository extends CrudRepository<Orders, Long> {

    Orders findById(long id);

    @Query("from Orders where customer.id=:id")
    List<Orders> findAllOrderForCustomerId(@Param("id") long customerId);

    @Query("from Orders")
    List<Orders> findAllOrders();
}
