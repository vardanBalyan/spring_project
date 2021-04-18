package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.orders.OrderProduct;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface OrderProductRepository extends CrudRepository<OrderProduct, Long> {

    OrderProduct findById(long id);

    @Query("select orders.id from OrderProduct where id=:id")
    long getOrderIdForOrderProductId(@Param("id") long id);
}
