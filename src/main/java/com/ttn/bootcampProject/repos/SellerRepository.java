package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.Seller;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SellerRepository extends CrudRepository<Seller, Long> {

    @Query(value = "select user_id from seller", nativeQuery = true)
    public List<Long> fetchAllIdsOfSellers();

    @Query(value = "select * from seller where user_id=:id", nativeQuery = true)
    public Seller findSellerByUserId(@Param("id") long id);
}
