package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.products.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Integer> {
}
