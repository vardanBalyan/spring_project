package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.products.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {

    @Query(value = "select DISTINCT brand from product where category_id=:id",nativeQuery = true)
    List<String> getAllUniqueBrandNamesForCategoryId(@Param("id") long id);

    @Query(value = "select * from product where seller_user_id=:sellerId AND category_id=:categoryId " +
            "AND brand=:brandName AND name=:productName", nativeQuery = true)
    Product getProductByCombination(@Param("sellerId") long sellerId, @Param("categoryId") long categoryId
            ,@Param("brandName") String brandName, @Param("productName") String productName);

    @Query(value = "select id from product where seller_user_id=:id AND is_deleted=false", nativeQuery = true)
    List<Long> getAllProductIdsForSellerId(@Param("id") long id);


    @Query(value = "select category_id from product where id=:productId",nativeQuery = true)
    long getCategoryIdForAProductId(@Param("productId") long productId);

    Product findById(long id);

    @Query(value = "select * from product where seller_user_id=:id AND is_deleted=false",nativeQuery = true)
    List<Product> getAllProductsOfSeller(@Param("id") long sellerId);

    @Query("from Product where isDeleted=false AND is_active=true")
    List<Product> findAllNonDeletedAndActiveProducts();

    @Query(value = "select * from product where category_id=:id AND is_deleted=false AND has_variation=true " +
            "AND is_active=true",nativeQuery = true)
    List<Product> findAllNonDeletedActiveProductWithHasVariationByCategoryId(@Param("id") long categoryId);


    @Query(value = "select * from product where id=:id AND is_deleted=false AND is_active=true", nativeQuery = true)
    Product findNonDeletedActiveProductById(@Param("id") long id);


    @Query(value = "select * from product where category_id=:categoryId AND is_deleted=false AND has_variation=true " +
            "AND is_active=true AND id NOT IN (:productId)",nativeQuery = true)
    List<Product> findAllSimilarProductWithHasVariationByCategoryId(@Param("categoryId") long categoryId
            , @Param("productId") long productId);


    @Query(value = "select * from product where category_id=:id AND is_deleted=false AND has_variation=true"
            ,nativeQuery = true)
    List<Product> findAllNonDeletedProductWithHasVariationByCategoryId(@Param("id") long categoryId);
}
