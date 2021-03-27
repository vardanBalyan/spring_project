package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.Customer;
import com.ttn.bootcampProject.entities.Seller;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    @Query(value = "select user_id from customer", nativeQuery = true)
    public List<Long> fetchAllIdsOfCustomers();

    @Query(value = "select * from customer where user_id=:id", nativeQuery = true)
    public Customer findCustomerById(@Param("id") long id);
}
