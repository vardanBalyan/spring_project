package com.ttn.bootcampProject.services;

import com.ttn.bootcampProject.dtos.*;
import com.ttn.bootcampProject.entities.Seller;
import com.ttn.bootcampProject.entities.User;
import com.ttn.bootcampProject.entities.products.Product;
import com.ttn.bootcampProject.entities.products.ProductVariation;
import com.ttn.bootcampProject.entities.products.categories.Category;
import com.ttn.bootcampProject.exceptions.ProductNotFoundException;
import com.ttn.bootcampProject.exceptions.ProductVariationNotFoundException;
import com.ttn.bootcampProject.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.*;

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
    @Autowired
    ProductVariationRepository productVariationRepository ;
    @Autowired
    CategoryMetadataFieldRepository categoryMetadataFieldRepository;
    @Autowired
    CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;

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


    public ResponseEntity<String> updateProduct(AddProductDto updateProduct, long id, String email)
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

        if(updateProduct.getName().equals(product.getName()))
        {
            return new ResponseEntity("Current name and new name of product should not be same.",HttpStatus.BAD_REQUEST);
        }

        Product nameCheck = productRepository.getProductByCombination(seller.getId()
                , productRepository.getCategoryIdForAProductId(product.getId())
        , product.getBrand(), updateProduct.getName());

        if(nameCheck != null)
        {
            return new ResponseEntity("A product already exist with same name.",HttpStatus.BAD_REQUEST);
        }


        product.setName(updateProduct.getName());
        product.setDescription(updateProduct.getDescription());
        product.setReturnable(updateProduct.isReturnable());
        product.setCancellable(updateProduct.isCancellable());

        productRepository.save(product);
        return new ResponseEntity("Product updated successfully.",HttpStatus.ACCEPTED);
    }

    public ResponseEntity<String> addAProductVariation(AddProductVariationDto addProductVariationDto, String email)
    {
        // finding user by email getting from principal
        User user = userRepository.findByEmail(email);
        // finding the seller from user id we got from email
        Seller seller = sellerRepository.findSellerByUserId(user.getId());

        List<Long> allProductIdsOfLoggedInSeller = productRepository.getAllProductIdsForSellerId(seller.getId());

        Product product = productRepository.findById(addProductVariationDto.getProductId());


        if(!allProductIdsOfLoggedInSeller.contains(addProductVariationDto.getProductId()) || product.isDeleted())
        {
            return new ResponseEntity("No product found with particular product id.",HttpStatus.NOT_FOUND);
        }

        if(!product.isActive())
        {
            return new ResponseEntity("Product is not active.",HttpStatus.BAD_REQUEST);
        }

        // getting the category id to use in metadata validation
        long categoryId = productRepository.getCategoryIdForAProductId(product.getId());


        // metadata map validation
        for (Map.Entry<String,String> pair: addProductVariationDto.getMetadata().entrySet()) {

            // getting metadata field id using the name of field
            Long metadataFieldId = categoryMetadataFieldRepository.findIdByName(pair.getKey());

            // validating metadata field name
            if(metadataFieldId == null)
            {
                return new ResponseEntity("No field exist with name as "+pair.getKey(),HttpStatus.BAD_REQUEST);
            }

            // getting the corresponding values of metadata field and category
            String values = categoryMetadataFieldValuesRepository.findValueByCompositeId(categoryId, metadataFieldId);

            // split the string since all values are stored as single string separated by commas
            // and storing in string array
            String[] valuesArray = values.split(",");

            List<String> valuesFetchedFromArray = new ArrayList<>();

            // converting the string array to a arraylist so i can use the contains() method
            valuesFetchedFromArray.addAll(Arrays.asList(valuesArray));

            // validating the entered field value
            if(!valuesFetchedFromArray.contains(pair.getValue()))
            {
                return new ResponseEntity("Invalid field value "+pair.getValue(),HttpStatus.BAD_REQUEST);
            }
        }

        List<ProductVariation> productVariations = product.getProductVariationList();

        ProductVariation productVariation = new ProductVariation();
        productVariation.setPrice(addProductVariationDto.getPrice());
        productVariation.setQuantityAvailable(addProductVariationDto.getQuantity());
        productVariation.setPrimaryImageName(addProductVariationDto.getPrimaryImageName());
        productVariation.setMetadata(addProductVariationDto.getMetadata());

        productVariations.add(productVariation);
        product.setProductVariationList(productVariations);
        productRepository.save(product);

        return new ResponseEntity("New product variation created.",HttpStatus.CREATED);
    }


    public DisplayProductVariationDto viewAProductVariation(long id, String email)
    {
        ProductVariation productVariation = productVariationRepository.findById(id);

        if(productVariation == null)
        {
            throw new ProductVariationNotFoundException("No product variation found with for the provided id.");
        }
        // finding user by email getting from principal
        User user = userRepository.findByEmail(email);
        // finding the seller from user id we got from email
        Seller seller = sellerRepository.findSellerByUserId(user.getId());

        List<Long> allProductIdsOfLoggedInSeller = productRepository.getAllProductIdsForSellerId(seller.getId());

        Product product = productRepository.findById(productVariationRepository.getProductIdForVariationId(id));

        if(!allProductIdsOfLoggedInSeller.contains(product.getId()) || product.isDeleted())
        {
            throw new ProductNotFoundException("Particular product variation not found for your products.");
        }

        Category productCategory = categoryRepository.findById(productRepository.getCategoryIdForAProductId(product.getId()));

        DisplayProductVariationDto displayProductVariationDto = new DisplayProductVariationDto();
        DisplayProductDto displayProductDto = new DisplayProductDto();

        displayProductDto.setCategoryId(productCategory.getId());
        displayProductDto.setCategoryName(product.getName());
        displayProductDto.setActive(product.isActive());
        displayProductDto.setReturnable(product.isReturnable());
        displayProductDto.setCancellable(product.isCancellable());
        displayProductDto.setName(product.getName());
        displayProductDto.setBrand(product.getBrand());
        displayProductDto.setDescription(product.getDescription());
        displayProductDto.setId(product.getId());

        displayProductVariationDto.setProduct(displayProductDto);

        displayProductVariationDto.setId(productVariation.getId());
        displayProductVariationDto.setPrice(productVariation.getPrice());
        displayProductVariationDto.setActive(productVariation.isActive());
        displayProductVariationDto.setPrimaryImageName(productVariation.getPrimaryImageName());
        displayProductVariationDto.setQuantityAvailable(productVariation.getQuantityAvailable());
        displayProductVariationDto.setMetadata(productVariation.getMetadata());

        return displayProductVariationDto;
    }

    public List<DisplayProductVariationDto> viewAllProductVariation(String email)
    {
        // finding user by email getting from principal
        User user = userRepository.findByEmail(email);
        // finding the seller from user id we got from email
        Seller seller = sellerRepository.findSellerByUserId(user.getId());

        List<DisplayProductVariationDto> displayProductVariationDtoList = new ArrayList<>();
        List<Product> allProductOfSeller = productRepository.getAllProductsOfSeller(seller.getId());

        for (Product product: allProductOfSeller) {

            List<ProductVariation> productVariations = productVariationRepository.getAllProductVariationForProductId(product.getId());

            for (ProductVariation productVariation: productVariations) {

                Category productCategory = categoryRepository.findById(productRepository.getCategoryIdForAProductId(product.getId()));

                DisplayProductVariationDto displayProductVariationDto = new DisplayProductVariationDto();
                DisplayProductDto displayProductDto = new DisplayProductDto();

                displayProductDto.setCategoryId(productCategory.getId());
                displayProductDto.setCategoryName(product.getName());
                displayProductDto.setActive(product.isActive());
                displayProductDto.setReturnable(product.isReturnable());
                displayProductDto.setCancellable(product.isCancellable());
                displayProductDto.setName(product.getName());
                displayProductDto.setBrand(product.getBrand());
                displayProductDto.setDescription(product.getDescription());
                displayProductDto.setId(product.getId());

                displayProductVariationDto.setProduct(displayProductDto);

                displayProductVariationDto.setId(productVariation.getId());
                displayProductVariationDto.setPrice(productVariation.getPrice());
                displayProductVariationDto.setActive(productVariation.isActive());
                displayProductVariationDto.setPrimaryImageName(productVariation.getPrimaryImageName());
                displayProductVariationDto.setQuantityAvailable(productVariation.getQuantityAvailable());
                displayProductVariationDto.setMetadata(productVariation.getMetadata());

                displayProductVariationDtoList.add(displayProductVariationDto);
            }

        }
        return displayProductVariationDtoList;
    }


    public ResponseEntity<String> updateProductVariation(AddProductVariationDto addProductVariationDto, String email, long id)
    {

        ProductVariation productVariation = productVariationRepository.findById(id);

        if(productVariation == null)
        {
            throw new ProductVariationNotFoundException("No product variation found with for the provided id.");
        }
        // finding user by email getting from principal
        User user = userRepository.findByEmail(email);
        // finding the seller from user id we got from email
        Seller seller = sellerRepository.findSellerByUserId(user.getId());

        List<Long> allProductIdsOfLoggedInSeller = productRepository.getAllProductIdsForSellerId(seller.getId());

        Product product = productRepository.findById(addProductVariationDto.getProductId());

        System.out.println(">>>>>>"+addProductVariationDto.getProductId());
        System.out.println(">>>>>>>>>"+allProductIdsOfLoggedInSeller);
        if(!allProductIdsOfLoggedInSeller.contains(addProductVariationDto.getProductId()) || product.isDeleted())
        {
            return new ResponseEntity("No product found with particular product id.",HttpStatus.NOT_FOUND);
        }


//        if(!product.isActive())
//        {
//            return new ResponseEntity("Product is not active.",HttpStatus.BAD_REQUEST);
//        }

        // getting the category id to use in metadata validation
        long categoryId = productRepository.getCategoryIdForAProductId(product.getId());


        // metadata map validation
        for (Map.Entry<String,String> pair: addProductVariationDto.getMetadata().entrySet()) {

            // getting metadata field id using the name of field
            Long metadataFieldId = categoryMetadataFieldRepository.findIdByName(pair.getKey());

            // validating metadata field name
            if(metadataFieldId == null)
            {
                return new ResponseEntity("No field exist with name as "+pair.getKey(),HttpStatus.BAD_REQUEST);
            }

            // getting the corresponding values of metadata field and category
            String values = categoryMetadataFieldValuesRepository.findValueByCompositeId(categoryId, metadataFieldId);

            // split the string since all values are stored as single string separated by commas
            // and storing in string array
            String[] valuesArray = values.split(",");

            List<String> valuesFetchedFromArray = new ArrayList<>();

            // converting the string array to a arraylist so i can use the contains() method
            valuesFetchedFromArray.addAll(Arrays.asList(valuesArray));

            // validating the entered field value
            if(!valuesFetchedFromArray.contains(pair.getValue()))
            {
                return new ResponseEntity("Invalid field value "+pair.getValue(),HttpStatus.BAD_REQUEST);
            }
        }

        productVariation.setPrice(addProductVariationDto.getPrice());
        productVariation.setQuantityAvailable(addProductVariationDto.getQuantity());
        productVariation.setPrimaryImageName(addProductVariationDto.getPrimaryImageName());
        productVariation.setMetadata(addProductVariationDto.getMetadata());

        productVariationRepository.save(productVariation);

        return new ResponseEntity("Product updated successfully.",HttpStatus.CREATED);
    }

    public ResponseEntity<String> activateAProduct(long id)
    {
        Product product = productRepository.findById(id);

        if(product == null)
        {
            return new ResponseEntity("No product found for the provided product id.",HttpStatus.BAD_REQUEST);
        }

        if(product.isActive())
        {
            return new ResponseEntity("Product is already active.",HttpStatus.BAD_REQUEST);
        }

        product.setActive(true);
        productRepository.save(product);

        return new ResponseEntity("Product is now active.",HttpStatus.ACCEPTED);
    }

    public ResponseEntity<String> deactivateAProduct(long id)
    {
        Product product = productRepository.findById(id);

        if(product == null)
        {
            return new ResponseEntity("No product found for the provided product id.",HttpStatus.BAD_REQUEST);
        }

        if(!product.isActive())
        {
            return new ResponseEntity("Product is already de-active.",HttpStatus.BAD_REQUEST);
        }

        product.setActive(false);
        productRepository.save(product);

        return new ResponseEntity("Product is now de-active.",HttpStatus.ACCEPTED);
    }


    public ProductWithVariationImageDto viewAProductForAdmin(long id)
    {
        Product product = productRepository.findById(id);

        if(product == null)
        {
            throw new ProductNotFoundException("No product found for the provided product id.");
        }

        if(product.isDeleted())
        {
            throw new ProductNotFoundException("No product found for the provided product id.");
        }

        Category productCategory = categoryRepository.findById(productRepository.getCategoryIdForAProductId(product.getId()));
        List<ProductVariation> productVariations = productVariationRepository.getAllProductVariationForProductId(product.getId());

        ProductWithVariationImageDto productWithVariationImageDto= new ProductWithVariationImageDto();
        productWithVariationImageDto.setId(product.getId());
        productWithVariationImageDto.setName(product.getName());
        productWithVariationImageDto.setBrand(product.getBrand());
        productWithVariationImageDto.setDescription(product.getDescription());
        productWithVariationImageDto.setActive(product.isActive());
        productWithVariationImageDto.setCancellable(product.isCancellable());
        productWithVariationImageDto.setReturnable(product.isReturnable());
        productWithVariationImageDto.setCategoryId(productCategory.getId());
        productWithVariationImageDto.setCategoryName(productCategory.getName());

        Map<Long,String> productVariationImages = new HashMap<>();

        for (ProductVariation variation : productVariations) {
            productVariationImages.put(variation.getId(), variation.getPrimaryImageName());
        }
        productWithVariationImageDto.setImageMap(productVariationImages);

        return productWithVariationImageDto;
    }


    public List<ProductWithVariationImageDto> viewAllProductForAdmin()
    {
        List<ProductWithVariationImageDto> productWithVariationImageDtoList = new ArrayList<>();
        List<Product> productList = productRepository.findAllNonDeletedProducts();

        for (Product product: productList) {
            Category productCategory = categoryRepository.findById(productRepository.getCategoryIdForAProductId(product.getId()));
            List<ProductVariation> productVariations = productVariationRepository.getAllProductVariationForProductId(product.getId());

            ProductWithVariationImageDto productWithVariationImageDto= new ProductWithVariationImageDto();
            productWithVariationImageDto.setId(product.getId());
            productWithVariationImageDto.setName(product.getName());
            productWithVariationImageDto.setBrand(product.getBrand());
            productWithVariationImageDto.setDescription(product.getDescription());
            productWithVariationImageDto.setActive(product.isActive());
            productWithVariationImageDto.setCancellable(product.isCancellable());
            productWithVariationImageDto.setReturnable(product.isReturnable());
            productWithVariationImageDto.setCategoryId(productCategory.getId());
            productWithVariationImageDto.setCategoryName(productCategory.getName());

            Map<Long,String> productVariationImages = new HashMap<>();

            for (ProductVariation variation : productVariations) {
                productVariationImages.put(variation.getId(), variation.getPrimaryImageName());
            }
            productWithVariationImageDto.setImageMap(productVariationImages);
            productWithVariationImageDtoList.add(productWithVariationImageDto);
        }

        return productWithVariationImageDtoList;
    }
}
