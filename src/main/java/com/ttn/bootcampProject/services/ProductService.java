package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.dtos.AddProductDto;
import com.ttn.bootcampProject.dtos.DisplayProductDto;
import com.ttn.bootcampProject.entities.Seller;
import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.entities.products.Product;
import com.ttn.bootcampProject.entities.products.categories.Category;
import com.ttn.bootcampProject.repos.CategoryRepository;
import com.ttn.bootcampProject.repos.ProductRepository;
import com.ttn.bootcampProject.repos.SellerRepository;
import com.ttn.bootcampProject.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductService {

    @Autowired
    SellerRepository sellerRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;

    public ResponseEntity<String> addAProduct(AddProductDto addProductDto, String email)
    {
        // finding user by email getting from principal
        User user = userRepository.findByEmail(email);
        // finding the seller from user id we got from email
        Seller seller = sellerRepository.findSellerByUserId(user.getId());
        List<Product> sellerProductList = seller.getProductList();

        Category category = categoryRepository.findById(addProductDto.getCategoryId());
        System.out.println(">>>>>>>>"+addProductDto.isReturnable());
        System.out.println(">>>>>>>"+addProductDto.isCancellable());

        if(category == null)
        {
            return new ResponseEntity("Invalid category id. No category found for the provided category id."
                    ,HttpStatus.NOT_FOUND);
        }

        if(category.isHasChild())
        {
            return new ResponseEntity("Category provided is not a leaf category.",HttpStatus.BAD_REQUEST);
        }

        Product productCheck = productRepository.getProductByCombination(seller.getId(), category.getId()
                , addProductDto.getBrand().toLowerCase(), addProductDto.getName().toLowerCase());

        if(productCheck != null)
        {
            return new ResponseEntity("A product already exist with same name.",HttpStatus.BAD_REQUEST);
        }

        List<Product> categoryProductList = category.getProductList();
        Product product = new Product();
        product.setName(addProductDto.getName().toLowerCase());
        product.setBrand(addProductDto.getBrand().toLowerCase());
        product.setDescription(addProductDto.getDescription());
        product.setCancellable(addProductDto.isCancellable());
        product.setReturnable(addProductDto.isReturnable());

        sellerProductList.add(product);
        categoryProductList.add(product);
        seller.setProductList(sellerProductList);
        category.setProductList(categoryProductList);

        productRepository.save(product);
        sellerRepository.save(seller);
        categoryRepository.save(category);

        return new ResponseEntity("Product saves successfully.",HttpStatus.CREATED);
    }

    public DisplayProductDto viewAProduct(long id, String email)
    {
        // finding user by email getting from principal
        User user = userRepository.findByEmail(email);
        // finding the seller from user id we got from email
        Seller seller = sellerRepository.findSellerByUserId(user.getId());

        List<Long> allProductIdsOfLoggedInSeller = productRepository.getAllProductIdsForSellerId(seller.getId());

        if(!allProductIdsOfLoggedInSeller.contains(id))
        {
            return null;
        }

        Category productCategory = categoryRepository.findById(productRepository.getCategoryIdForAProductId(id));
        Product product = productRepository.findById(id);

        DisplayProductDto displayProductDto = new DisplayProductDto();
        displayProductDto.setId(product.getId());
        displayProductDto.setName(product.getName());
        displayProductDto.setBrand(product.getBrand());
        displayProductDto.setDescription(product.getDescription());
        displayProductDto.setActive(product.isActive());
        displayProductDto.setCancellable(product.isCancellable());
        displayProductDto.setReturnable(product.isReturnable());
        displayProductDto.setCategoryId(productCategory.getId());
        displayProductDto.setCategoryName(productCategory.getName());
        return displayProductDto;
    }

    public List<DisplayProductDto> viewALlProducts(String email)
    {
        // finding user by email getting from principal
        User user = userRepository.findByEmail(email);
        // finding the seller from user id we got from email
        Seller seller = sellerRepository.findSellerByUserId(user.getId());

        List<Product> allProductOfSeller = productRepository.getAllProductsOfSeller(seller.getId());

        List<DisplayProductDto> displayProductDtoList = new ArrayList<>();

        for (Product product: allProductOfSeller) {

            DisplayProductDto displayProductDto = new DisplayProductDto();
            Category productCategory = categoryRepository.findById(productRepository.getCategoryIdForAProductId(product.getId()));

            displayProductDto.setId(product.getId());
            displayProductDto.setName(product.getName());
            displayProductDto.setBrand(product.getBrand());
            displayProductDto.setDescription(product.getDescription());
            displayProductDto.setActive(product.isActive());
            displayProductDto.setCancellable(product.isCancellable());
            displayProductDto.setReturnable(product.isReturnable());
            displayProductDto.setCategoryId(productCategory.getId());
            displayProductDto.setCategoryName(productCategory.getName());

            displayProductDtoList.add(displayProductDto);
        }

        return displayProductDtoList;
    }

    public ResponseEntity<String> deleteAProduct(long id, String email)
    {
        // finding user by email getting from principal
        User user = userRepository.findByEmail(email);
        // finding the seller from user id we got from email
        Seller seller = sellerRepository.findSellerByUserId(user.getId());

        List<Long> allProductIdsOfLoggedInSeller = productRepository.getAllProductIdsForSellerId(seller.getId());

        if(!allProductIdsOfLoggedInSeller.contains(id))
        {
            return new ResponseEntity("No product found with particular product id.",HttpStatus.NOT_FOUND);
        }

        Product product = productRepository.findById(id);
        product.setDeleted(true);
        productRepository.save(product);

        return new ResponseEntity("Product deleted successfully.",HttpStatus.ACCEPTED);
    }
}
