package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.products.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {

    @Query("select name from Product where brand=:brandName")
    List<String> allProductNamesForABrandName(@Param("brandName") String brandName);

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

    @Query("from Product where isDeleted=false")
    List<Product> findAllNonDeletedProducts();
}
