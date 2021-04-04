package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.dtos.AddCategoryDto;
import com.ttn.bootcampProject.dtos.CategoryMetadataFieldValuesDto;
import com.ttn.bootcampProject.entities.products.categories.Category;
import com.ttn.bootcampProject.entities.products.categories.CategoryMetadataField;
import com.ttn.bootcampProject.entities.products.categories.CategoryMetadataFieldValues;
import com.ttn.bootcampProject.entities.products.categories.CategoryMetadataFieldValuesId;
import com.ttn.bootcampProject.repos.CategoryMetadataFieldRepository;
import com.ttn.bootcampProject.repos.CategoryMetadataFieldValuesRepository;
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
    @Autowired
    CategoryMetadataFieldRepository categoryMetadataFieldRepository;
    @Autowired
    CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;

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

    public ResponseEntity<String> addMetadataField(String fieldName)
    {
        List<String> fieldNamesList = categoryMetadataFieldRepository.findAllFieldNames();

        if(fieldNamesList.contains(fieldName.toLowerCase()))
        {
            return new ResponseEntity("Metadata filed already exist. Please give a unique name."
                    ,HttpStatus.BAD_REQUEST);
        }
        CategoryMetadataField metadataField = new CategoryMetadataField();
        metadataField.setName(fieldName.toLowerCase());
        categoryMetadataFieldRepository.save(metadataField);
        return new ResponseEntity("Added new category metadata field.",HttpStatus.ACCEPTED);
    }

    public List<CategoryMetadataField> viewAllMetadataFields()
    {
        return categoryMetadataFieldRepository.allMetadataFields();
    }

    public ResponseEntity<String> addCategoryMetadataFieldValues(CategoryMetadataFieldValuesDto categoryMetadataFieldValuesDto)
    {
        CategoryMetadataField categoryMetadata = categoryMetadataFieldRepository
                .findById(categoryMetadataFieldValuesDto.getMetadataId());
        Category category = categoryRepository
                .findById(categoryMetadataFieldValuesDto.getCategoryId());

        if(categoryMetadata == null)
        {
            return new ResponseEntity("No metadata found for the provided metadata id."
                    ,HttpStatus.NOT_FOUND);
        }

        if(category == null)
        {
            return new ResponseEntity("No category found for the provided category id."
                    ,HttpStatus.NOT_FOUND);
        }

        if(category.isHasChild())
        {
            return new ResponseEntity("Category id should be of leaf node category."
                    ,HttpStatus.BAD_REQUEST);
        }

        CategoryMetadataFieldValues metadataFieldValues = new CategoryMetadataFieldValues();

        CategoryMetadataFieldValuesId metadataFieldValuesId = new CategoryMetadataFieldValuesId();
        metadataFieldValuesId.setCategoryId(category.getId());
        metadataFieldValuesId.setCategoryMetadataFieldId(categoryMetadata.getId());

        metadataFieldValues.setId(metadataFieldValuesId);
        metadataFieldValues.setCategory(category);
        metadataFieldValues.setCategoryMetadataField(categoryMetadata);
        metadataFieldValues.setValue(categoryMetadataFieldValuesDto.getValues());
        categoryMetadataFieldValuesRepository.save(metadataFieldValues);
        return new ResponseEntity("New metadata field values created successfully."
                ,HttpStatus.CREATED);
    }

    public ResponseEntity<String> updateCategoryMetadataFieldValues(CategoryMetadataFieldValuesDto categoryMetadataFieldValuesDto)
    {
        CategoryMetadataFieldValues metadataFieldValues = categoryMetadataFieldValuesRepository
                .findByMetadataCompositeId(categoryMetadataFieldValuesDto.getCategoryId()
                        ,categoryMetadataFieldValuesDto.getMetadataId());

        if(metadataFieldValues == null)
        {
            return new ResponseEntity("No metadata field values found for the provided category and metadata id."
                    ,HttpStatus.NOT_FOUND);
        }
        metadataFieldValues.setValue(categoryMetadataFieldValuesDto.getValues());
        categoryMetadataFieldValuesRepository.save(metadataFieldValues);
        return new ResponseEntity("Values updated successfully.",HttpStatus.ACCEPTED);
    }
}
