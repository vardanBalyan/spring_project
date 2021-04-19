package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.orders.Cart;
import com.ttn.bootcampProject.entities.orders.CartId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface CartRepository extends CrudRepository<Cart, CartId> {

    @Query("from Cart where cartId.customerUserId=:customerId")
    List<Cart> getAllCartItemsForCustomerId(@Param("customerId") long customerId, Pageable pageable);

    @Query("from Cart where cartId.customerUserId=:customerId AND cartId.productVariationId=:variationId")
    Cart getItemFromCartForCompositeKeyCombination(@Param("customerId") long customerId
            , @Param("variationId") long variationId);

    @Query("select cartId.productVariationId from Cart where cartId.customerUserId=:customerId")
    List<Long> getListOfProductVariationIdForCustomerId(@Param("customerId") long customerId);

    @Modifying
    @Query("delete from Cart where cartId.customerUserId=:customerId AND cartId.productVariationId IN (:variationIds)")
    void deleteCustomerItemsFromCart(@Param("customerId") long customerId, @Param("variationIds") List<Long> variationIds);


    @Query("select quantity from Cart where cartId.customerUserId=:customerId AND cartId.productVariationId=:variationId")
    int getQuantityForCustomerIdAndVariationId(@Param("customerId") long customerId
            , @Param("variationId") long variationId);
}
