package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.products.ProductVariation;
import org.springframework.data.repository.CrudRepository;

public interface ProductVariationRepository extends CrudRepository<ProductVariation, Integer> {
}
