package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.products.categories.CategoryMetadataFieldValues;
import com.ttn.bootcampProject.entities.products.categories.CategoryMetadataFieldValuesId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PatchMapping;

public interface CategoryMetadataFieldValuesRepository extends CrudRepository<CategoryMetadataFieldValues, CategoryMetadataFieldValuesId> {

    @Query("from CategoryMetadataFieldValues where id.categoryId=:cid AND id.categoryMetadataFieldId=:mid")
    CategoryMetadataFieldValues findByMetadataCompositeId(@Param("cid") long categoryId, @Param("mid") long metadataId);

    @Query("select value from CategoryMetadataFieldValues where id.categoryId=:cid AND id.categoryMetadataFieldId=:mid")
    String valueByCompositeId(@Param("cid") long categoryId, @Param("mid") long metadataId);
}
