package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.products.categories.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
}
