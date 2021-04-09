package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.dtos.*;
import com.ttn.bootcampProject.entities.products.Product;
import com.ttn.bootcampProject.entities.products.ProductVariation;
import com.ttn.bootcampProject.entities.products.categories.Category;
import com.ttn.bootcampProject.entities.products.categories.CategoryMetadataField;
import com.ttn.bootcampProject.entities.products.categories.CategoryMetadataFieldValues;
import com.ttn.bootcampProject.entities.products.categories.CategoryMetadataFieldValuesId;
import com.ttn.bootcampProject.exceptions.CategoryNotFoundException;
import com.ttn.bootcampProject.exceptions.ProductNotFoundException;
import com.ttn.bootcampProject.repos.*;
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
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductVariationRepository productVariationRepository;

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

    public AllCategoryInfoDto viewACategory(long id)
    {
        Category category = categoryRepository.findById(id);

        if(category == null)
        {
            return null;
        }

        AllCategoryInfoDto categoryInfoDto = new AllCategoryInfoDto();

        // getting all parent chain details and assigning to dto field
        categoryInfoDto.setParentChain(getParentChainInfo(categoryRepository.findParentIdByCategoryId(category.getId())));

        categoryInfoDto.setId(category.getId());
        categoryInfoDto.setCategoryName(category.getName());

        // getting all children of category
       Set<Category> childCategorySet = category.getCategorySet();
        // getting all metadata field
        List<CategoryMetadataField> allCategoryMetadata = categoryMetadataFieldRepository.allMetadataFields();
        List<DisplayCategoryDto> displayCategoryDtoList = new ArrayList<>();

        // to display information of all children with metadata and values
        for (Category child: childCategorySet) {
            DisplayCategoryDto displayCategoryDto = new DisplayCategoryDto();

            displayCategoryDto.setCategoryName(child.getName());
            displayCategoryDto.setId(child.getId());

            // getting map of Metadata and values for the child
            displayCategoryDto.setMetadataFieldsAndValues(getMapOfMetadataAndValues(child, allCategoryMetadata));

            displayCategoryDto.setParentChain(getParentChainInfo(categoryRepository.findParentIdByCategoryId(child.getId())));
            displayCategoryDtoList.add(displayCategoryDto);
        }

        categoryInfoDto.setImmediateChildList(displayCategoryDtoList);
        return categoryInfoDto;
    }

    public List<AllCategoryInfoDto> viewAllCategoryAdmin()
    {
        List<AllCategoryInfoDto> allCategoryInfoDtoList = new ArrayList<>();
        List<Category> allCategories = categoryRepository.getAllCategories();

        for (Category category: allCategories) {
            AllCategoryInfoDto categoryInfoDto = new AllCategoryInfoDto();

            // getting all parent chain details and assigning to dto field
            categoryInfoDto.setParentChain(getParentChainInfo(categoryRepository.findParentIdByCategoryId(category.getId())));

            categoryInfoDto.setId(category.getId());
            categoryInfoDto.setCategoryName(category.getName());

            // getting all children of category
            Set<Category> childCategorySet = category.getCategorySet();
            // getting all metadata field
            List<CategoryMetadataField> allCategoryMetadata = categoryMetadataFieldRepository.allMetadataFields();
            List<DisplayCategoryDto> displayCategoryDtoList = new ArrayList<>();

            // to display information of all children with metadata and values
            for (Category child: childCategorySet) {
                DisplayCategoryDto displayCategoryDto = new DisplayCategoryDto();

                displayCategoryDto.setCategoryName(child.getName());
                displayCategoryDto.setId(child.getId());

                // getting map of Metadata and values for the child
                displayCategoryDto.setMetadataFieldsAndValues(getMapOfMetadataAndValues(child, allCategoryMetadata));

                displayCategoryDto.setParentChain(getParentChainInfo(categoryRepository.findParentIdByCategoryId(child.getId())));
                displayCategoryDtoList.add(displayCategoryDto);
            }

            categoryInfoDto.setImmediateChildList(displayCategoryDtoList);
            allCategoryInfoDtoList.add(categoryInfoDto);
        }

        return allCategoryInfoDtoList;
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


    public List<DisplayCategoryDto> viewAllCategoryForSeller()
    {
        List<DisplayCategoryDto> displayCategoryDtoList = new ArrayList<>();

        // getting all leaf node categories
        List<Category> leafCategoryList = categoryRepository.getAllLeafCategory();

        // getting all metadata field
        List<CategoryMetadataField> allCategoryMetadata = categoryMetadataFieldRepository.allMetadataFields();

        for (Category leafCategory: leafCategoryList) {
            DisplayCategoryDto displayCategoryDto = new DisplayCategoryDto();

            displayCategoryDto.setCategoryName(leafCategory.getName());
            displayCategoryDto.setId(leafCategory.getId());

            displayCategoryDto.setMetadataFieldsAndValues(getMapOfMetadataAndValues(leafCategory, allCategoryMetadata));


            displayCategoryDto.setParentChain(getParentChainInfo(categoryRepository.findParentIdByCategoryId(leafCategory.getId())));
            displayCategoryDtoList.add(displayCategoryDto);
        }

        return displayCategoryDtoList;
    }


    public List<ViewAllCategoryForCustomerDto> viewCategoryForCustomer(Long id)
    {
        List<ViewAllCategoryForCustomerDto> categoryForCustomerList = new ArrayList<>();

        // if id is null then return all parent category
        if(id == 0)
        {
            List<Category> parentCategoryList = categoryRepository.getAllParentCategory();

            for (Category category: parentCategoryList) {
                ViewAllCategoryForCustomerDto categoryForCustomer = new ViewAllCategoryForCustomerDto();
                categoryForCustomer.setId(category.getId());
                categoryForCustomer.setName(category.getName());
                categoryForCustomerList.add(categoryForCustomer);
            }

            return categoryForCustomerList;
        }


        Category fetchedCategory = categoryRepository.findById(id);

        // checks if category exist with the id provided
        if(fetchedCategory == null)
        {
            throw new CategoryNotFoundException("No category found for provided id.");
        }
        else
        {
            // runs if the category we got is parent of another category
            if(fetchedCategory.isHasChild())
            {
                List<Category> childCategoryList = categoryRepository.findAllChildForParentId(fetchedCategory.getId());
                for (Category category: childCategoryList) {
                    ViewAllCategoryForCustomerDto categoryForCustomer = new ViewAllCategoryForCustomerDto();
                    categoryForCustomer.setName(category.getName());
                    categoryForCustomer.setId(category.getId());
                    categoryForCustomerList.add(categoryForCustomer);
                }
            }
            else
            {
                // runs when category we got is leaf category
                ViewAllCategoryForCustomerDto categoryForCustomer = new ViewAllCategoryForCustomerDto();
                categoryForCustomer.setName(fetchedCategory.getName());
                categoryForCustomer.setId(fetchedCategory.getId());
                categoryForCustomerList.add(categoryForCustomer);
            }
        }

        return categoryForCustomerList;
    }


    public FilterCategoryDto filterCategory(long categoryId)
    {
        FilterCategoryDto filterCategoryDto = new FilterCategoryDto();

        Category category = categoryRepository.findById(categoryId);

        if(category == null)
        {
            throw new CategoryNotFoundException("Invalid category id.");
        }

        if(category.isHasChild())
        {
            throw new CategoryNotFoundException("Category passed is not a leaf category.");
        }

        List<Product> allProductsOfCategory = productRepository
                .findAllNonDeletedProductWithHasVariationByCategoryId(category.getId());

        if(allProductsOfCategory.isEmpty())
        {
            throw new ProductNotFoundException("No product exist for this category");
        }

        List<CategoryMetadataField> allCategoryMetadata = categoryMetadataFieldRepository.allMetadataFields();
        List<String> brandNames = productRepository.getAllUniqueBrandNamesForCategoryId(category.getId());
        List<Double> priceList = new ArrayList<>();

        for (Product product: allProductsOfCategory) {
            List<ProductVariation> variationList = productVariationRepository
                    .getAllProductVariationForProductId(product.getId());

            for (ProductVariation variation: variationList) {
                priceList.add(variation.getPrice());
            }
        }

        Collections.sort(priceList);

        filterCategoryDto.setBrands(brandNames);
        filterCategoryDto.setMinPrice(priceList.get(0));
        filterCategoryDto.setMaxPrice(priceList.get(priceList.size()-1));
        filterCategoryDto.setMetadata(getMapOfMetadataAndValues(category, allCategoryMetadata));

        return filterCategoryDto;
    }

//--------------------------------------------------------------------------------------------------------//

    private String getParentChainInfo(Long parentId)
    {
        List<String> parentChainList = new ArrayList<>();

        // running while loop till we get parent id as null to reach root parent node
        while (parentId != null)
        {
            // getting parent of each child in hierarchy
            Category immediateParent = categoryRepository.findById(parentId);
            // adding the parent category names to the list
            parentChainList.add(immediateParent.getName());
            parentId = categoryRepository.findParentIdByCategoryId(immediateParent.getId());
        }

        Collections.reverse(parentChainList);

        // using string builder to append the names of all parent into single string
        StringBuilder parentChain = new StringBuilder();

        for (String parent: parentChainList) {
            // appending names to string builder
            parentChain.append(parent);
            parentChain.append(" > ");
        }

        return parentChain.toString();
    }

    private Map<String,String> getMapOfMetadataAndValues(Category category, List<CategoryMetadataField> metadataFieldList)
    {
        Map<String, String> metadataAndValuesMap = new HashMap<>();

        // assigning metadata and metadata values for the particular category
        for (CategoryMetadataField metadataField : metadataFieldList) {
            metadataAndValuesMap.put(metadataField.getName(), categoryMetadataFieldValuesRepository
                    .findValueByCompositeId(category.getId(), metadataField.getId()));
        }

        return metadataAndValuesMap;
    }
}
