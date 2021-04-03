package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.products.categories.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Integer> {

    Category findById(long id);

    @Query(value = "select parent_category_id from category where id=:id",nativeQuery = true)
    Long findParentIdByCategoryId(@Param("id") long id);

    @Query(value = "select * from category", nativeQuery = true)
    List<Category> getAllCategories();
}
