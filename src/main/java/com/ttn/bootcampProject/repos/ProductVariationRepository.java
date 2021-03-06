package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.products.ProductVariation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductVariationRepository extends CrudRepository<ProductVariation, Long> {

    @Query("from ProductVariation where id=:id")
    ProductVariation findProductVariationById(@Param("id") long id);

    ProductVariation findById(long id);

    @Query(value = "select product_id from product_variation where id=:id",nativeQuery = true)
     long getProductIdForVariationId(@Param("id") long id);

    @Query(value = "select * from product_variation where product_id=:productId",nativeQuery = true)
    List<ProductVariation> getAllProductVariationForProductId(@Param("productId") long productId);

    @Query(value = "select * from product_variation where product_id=:productId",nativeQuery = true)
    List<ProductVariation> getAllProductVariationForProductId(@Param("productId") long productId, Pageable pageable);

    @Query("from ProductVariation where id IN (:variationIds)")
    List<ProductVariation> getAllProductVariationByIdsList(@Param("variationIds") List<Long> variationIds);

    @Query(value = "select id from product_variation where product_id IN (:idList)", nativeQuery = true)
    List<Long> getAllVariationIdsForListOfProductId(@Param("idList") List<Long> productIdList);
}
