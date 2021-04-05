package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.dtos.AddCategoryDto;
import com.ttn.bootcampProject.dtos.CategoryMetadataFieldValuesDto;
import com.ttn.bootcampProject.dtos.ViewAllCategoryForCustomer;
import com.ttn.bootcampProject.dtos.ViewAllCategorySeller;
import com.ttn.bootcampProject.entities.products.categories.Category;
import com.ttn.bootcampProject.entities.products.categories.CategoryMetadataField;
import com.ttn.bootcampProject.entities.products.categories.CategoryMetadataFieldValues;
import com.ttn.bootcampProject.entities.products.categories.CategoryMetadataFieldValuesId;
import com.ttn.bootcampProject.exceptions.CategoryNotFoundException;
import com.ttn.bootcampProject.repos.CategoryMetadataFieldRepository;
import com.ttn.bootcampProject.repos.CategoryMetadataFieldValuesRepository;
import com.ttn.bootcampProject.repos.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.*;

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
            //adds the category as child category for the provided parent id

            Set<Category> childCategorySet = parentCategory.getCategorySet();
            childCategorySet.add(category);
            parentCategory.setCategorySet(childCategorySet);
            parentCategory.setHasChild(true);
            categoryRepository.save(parentCategory);
            return new ResponseEntity("Added as child category for specified parent category id."
                    ,HttpStatus.ACCEPTED);
        }

        // saves as new parent if parent id not provided
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
            categoryDto.setParentId(categoryRepository.findParentIdsByCategoryId(category.getId()));
            categoryDto.setHasChild(categoryDto.isHasChild());
            return categoryDto;
        }

        // returns null if category not found for the provided id
        return null;
    }

    public List<AddCategoryDto> viewAllCategoryAdmin()
    {
        // gets all categories from the database
        List<Category> categoryList = categoryRepository.getAllCategories();
        List<AddCategoryDto> categoryDtoList = new ArrayList<>();

        // assigning categories to the List of AddCategoryDto
        for (Category category:categoryList) {
            AddCategoryDto categoryDto = new AddCategoryDto();
            categoryDto.setId(category.getId());
            categoryDto.setName(category.getName());
            categoryDto.setParentId(categoryRepository.findParentIdsByCategoryId(category.getId()));
            categoryDto.setHasChild(category.isHasChild());
            categoryDtoList.add(categoryDto);
        }
        return categoryDtoList;
    }

    public ResponseEntity<String> updateCategory(AddCategoryDto categoryDto)
    {
        Category category = categoryRepository.findById(categoryDto.getId());

        // checks if category exist for the provided id

        if(category == null)
        {
            return new ResponseEntity("No category found for specified id.",HttpStatus.NOT_FOUND);
        }

        List<String> categoryNames = categoryRepository.getAllCategoryNames();

        // checks if category name already exist in the table
        if(categoryNames.contains(categoryDto.getName().toLowerCase()))
        {
            return new ResponseEntity("Category name already exist. Please give a unique name."
                    ,HttpStatus.BAD_REQUEST);
        }
        category.setName(categoryDto.getName().toLowerCase());
        categoryRepository.save(category);
        return new ResponseEntity("Category updated successfully.",HttpStatus.ACCEPTED);
    }

    public ResponseEntity<String> addMetadataField(String fieldName)
    {
        List<String> fieldNamesList = categoryMetadataFieldRepository.findAllFieldNames();

        // checks if metadata already exist in the table
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
        // getting metadata for the provided id
        CategoryMetadataField categoryMetadata = categoryMetadataFieldRepository
                .findById(categoryMetadataFieldValuesDto.getMetadataId());
        // getting category for the provided id
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

        // checking if the category is leaf node or not
        if(category.isHasChild())
        {
            return new ResponseEntity("Category id should be of leaf node category."
                    ,HttpStatus.BAD_REQUEST);
        }

        CategoryMetadataFieldValues metadataFieldValues = new CategoryMetadataFieldValues();

        // creating the composite key id for the CategoryMetadataFieldValues table
        CategoryMetadataFieldValuesId metadataFieldValuesId = new CategoryMetadataFieldValuesId();
        metadataFieldValuesId.setCategoryId(category.getId());
        metadataFieldValuesId.setCategoryMetadataFieldId(categoryMetadata.getId());

        // assigning parameters and saving
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
        // getting the metadata filed value for the provided category id and metadata id to check if exist or not
        CategoryMetadataFieldValues metadataFieldValues = categoryMetadataFieldValuesRepository
                .findByMetadataCompositeId(categoryMetadataFieldValuesDto.getCategoryId()
                        ,categoryMetadataFieldValuesDto.getMetadataId());

        if(metadataFieldValues == null)
        {
            return new ResponseEntity("No metadata field values found for the provided category and metadata id."
                    ,HttpStatus.NOT_FOUND);
        }

        // updating values
        metadataFieldValues.setValue(categoryMetadataFieldValuesDto.getValues());
        categoryMetadataFieldValuesRepository.save(metadataFieldValues);
        return new ResponseEntity("Values updated successfully.",HttpStatus.ACCEPTED);
    }


    public List<ViewAllCategorySeller> viewAllCategoryForSeller()
    {
        List<ViewAllCategorySeller> categorySellerList = new ArrayList<>();

        // getting all leaf node categories
        List<Category> leafCategoryList = categoryRepository.getAllLeafCategory();

        // getting all metadata field
        List<CategoryMetadataField> allCategoryMetadata = categoryMetadataFieldRepository.allMetadataFields();

        for (Category leafCategory: leafCategoryList) {
            ViewAllCategorySeller categorySeller = new ViewAllCategorySeller();

            categorySeller.setCategoryName(leafCategory.getName());

            Map<String, String> metadataAndValuesMap = new HashMap<>();

            // assigning metadata and metadata values for the particular leaf node category
            for (CategoryMetadataField metadataField: allCategoryMetadata) {
                metadataAndValuesMap.put(metadataField.getName(), categoryMetadataFieldValuesRepository
                        .valueByCompositeId(leafCategory.getId(), metadataField.getId()));
            }
            categorySeller.setMetadataFieldsAndValues(metadataAndValuesMap);
            categorySellerList.add(categorySeller);
        }

        return categorySellerList;
    }


    public List<ViewAllCategoryForCustomer> viewCategoryForCustomer(Long id)
    {
        List<ViewAllCategoryForCustomer> categoryForCustomerList = new ArrayList<>();

        // if id is null then return all parent category
        if(id == 0)
        {
            List<Category> parentCategoryList = categoryRepository.getAllParentCategory();

            for (Category category: parentCategoryList) {
                ViewAllCategoryForCustomer categoryForCustomer = new ViewAllCategoryForCustomer();
                categoryForCustomer.setId(category.getId());
                categoryForCustomer.setName(category.getName());
                categoryForCustomerList.add(categoryForCustomer);
            }

            return categoryForCustomerList;
        }


        Category checkCategory = categoryRepository.findById(id);

        // checks if category exist with the id provided
        if(checkCategory == null)
        {
            throw new CategoryNotFoundException("No category found for provided id.");
        }
        else
        {
            // runs if the category we got is parent of another category
            if(checkCategory.isHasChild())
            {
                List<Category> childCategoryList = categoryRepository.findAllChildForParentId(checkCategory.getId());
                for (Category category: childCategoryList) {
                    ViewAllCategoryForCustomer categoryForCustomer = new ViewAllCategoryForCustomer();
                    categoryForCustomer.setName(category.getName());
                    categoryForCustomer.setId(category.getId());
                    categoryForCustomerList.add(categoryForCustomer);
                }
            }
            else
            {
                // runs when category we got is leaf category
                ViewAllCategoryForCustomer categoryForCustomer = new ViewAllCategoryForCustomer();
                categoryForCustomer.setName(checkCategory.getName());
                categoryForCustomer.setId(checkCategory.getId());
                categoryForCustomerList.add(categoryForCustomer);
            }
        }

        return categoryForCustomerList;
    }
}
