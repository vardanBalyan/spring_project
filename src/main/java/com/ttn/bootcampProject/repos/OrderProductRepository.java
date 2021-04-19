package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.orders.OrderProduct;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderProductRepository extends CrudRepository<OrderProduct, Long> {

    OrderProduct findById(long id);

    @Query("select orders.id from OrderProduct where id=:id")
    long getOrderIdForOrderProductId(@Param("id") long id);

    @Query("from OrderProduct where orders.id=:id")
    List<OrderProduct> findOrderProductForOrderId(@Param("id") long id);

    @Query("from OrderProduct where productVariation.id IN (:idList) AND orders.id=:oid")
    List<OrderProduct> findByOrderIdAndVariationIdList(@Param("idList") List<Long> variationIdList, @Param("oid") long id);

    @Query(value = "select DISTINCT order_id from order_product where product_variation_id IN (:idList)",nativeQuery = true)
    List<Long> getAllOrderIdForVariationIdsList(@Param("idList") List<Long> variationIdList);
}
