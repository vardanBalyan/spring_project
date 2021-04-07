package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.products.ProductVariation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductVariationRepository extends CrudRepository<ProductVariation, Long> {

    ProductVariation findById(long id);

    @Query(value = "select product_id from product_variation where id=:id",nativeQuery = true)
     long getProductIdForVariationId(@Param("id") long id);

    @Query(value = "select * from product_variation where product_id=:productId",nativeQuery = true)
    List<ProductVariation> getAllProductVariationForProductId(@Param("productId") long productId);
}
