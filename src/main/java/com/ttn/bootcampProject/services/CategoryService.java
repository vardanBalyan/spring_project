package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.dtos.AddCategoryDto;
import com.ttn.bootcampProject.entities.products.categories.Category;
import com.ttn.bootcampProject.repos.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    public ResponseEntity<String> addCategory(AddCategoryDto categoryDto)
    {

        Category category = new Category();
        category.setName(categoryDto.getName());

        if(categoryDto.getParentId() != null)
        {
            Category parentCategory = categoryRepository.findById(categoryDto.getParentId());
            if(parentCategory == null)
            {
                return new ResponseEntity("No parent exist for the specified parent id."
                        ,HttpStatus.NOT_FOUND);
            }
            Set<Category> childCategorySet = parentCategory.getCategorySet();
            childCategorySet.add(category);
            parentCategory.setCategorySet(childCategorySet);
            parentCategory.setHasChild(true);
            categoryRepository.save(parentCategory);
            return new ResponseEntity("Added as child category for specified parent category id."
                    ,HttpStatus.ACCEPTED);
        }
        categoryRepository.save(category);
        return new ResponseEntity("Added as new parent category.",HttpStatus.ACCEPTED);
    }

    public AddCategoryDto viewACategory(long id)
    {
        Category category = categoryRepository.findById(id);

        if(category != null)
        {
            AddCategoryDto categoryDto = new AddCategoryDto();
            categoryDto.setName(category.getName());
            categoryDto.setParentId(categoryRepository.findParentIdByCategoryId(category.getId()));
            categoryDto.setHasChild(categoryDto.isHasChild());
            return categoryDto;
        }
        return null;
    }

    public List<AddCategoryDto> viewAllCategory()
    {
        List<Category> categoryList = categoryRepository.getAllCategories();
        List<AddCategoryDto> categoryDtoList = new ArrayList<>();

        for (Category category:categoryList) {
            AddCategoryDto categoryDto = new AddCategoryDto();
            categoryDto.setId(category.getId());
            categoryDto.setName(category.getName());
            categoryDto.setParentId(categoryRepository.findParentIdByCategoryId(category.getId()));
            categoryDto.setHasChild(category.isHasChild());
            categoryDtoList.add(categoryDto);
        }
        return categoryDtoList;
    }

    public ResponseEntity<String> updateCategory(AddCategoryDto categoryDto)
    {
        Category category = categoryRepository.findById(categoryDto.getId());

        if(category == null)
        {
            return new ResponseEntity("No category found for specified id.",HttpStatus.NOT_FOUND);
        }
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        return new ResponseEntity("Category updated successfully.",HttpStatus.ACCEPTED);
    }
}
