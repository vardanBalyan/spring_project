package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.orders.Cart;
import com.ttn.bootcampProject.entities.orders.CartId;
import org.springframework.data.repository.CrudRepository;

public interface CartRepository extends CrudRepository<Cart, CartId> {

}
