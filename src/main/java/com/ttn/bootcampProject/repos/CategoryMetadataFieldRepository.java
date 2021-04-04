package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.products.categories.CategoryMetadataField;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryMetadataFieldRepository extends CrudRepository<CategoryMetadataField, Long> {

    @Query("select name from CategoryMetadataField")
    List<String> findAllFieldNames();

    @Query("from CategoryMetadataField")
    List<CategoryMetadataField> allMetadataFields();

    CategoryMetadataField findById(long id);
}
