package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.products.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {

    @Query("select name from Product where brand=:brandName")
    List<String> allProductNamesForABrandName(@Param("brandName") String brandName);
}
