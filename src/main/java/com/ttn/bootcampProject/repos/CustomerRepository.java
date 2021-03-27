package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    @Query(value = "select user_id from customer", nativeQuery = true)
    public List<Long> fetchAllIdsOfCustomers();
}
