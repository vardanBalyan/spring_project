package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.products.categories.CategoryMetadataFieldValues;
import com.ttn.bootcampProject.entities.products.categories.CategoryMetadataFieldValuesId;
import org.springframework.data.repository.CrudRepository;

public interface CategoryMetadataFieldValuesRepository extends CrudRepository<CategoryMetadataFieldValues, CategoryMetadataFieldValuesId> {
}
