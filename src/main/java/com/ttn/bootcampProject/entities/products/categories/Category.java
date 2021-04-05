package com.ttn.bootcampProject.entities.products.categories;

import com.ttn.bootcampProject.entities.products.Product;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    //@Column(unique = true)
    private String name;
    private boolean hasChild;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_category_id")
    private Set<Category> categorySet;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private List<Product> productList;

    @OneToMany(mappedBy = "category")
    Set<CategoryMetadataFieldValues> categoryMetadataFieldValues;

    public void addCategoryMetadataFieldValues(CategoryMetadataFieldValues value)
    {
        if(value!=null)
        {
            if(categoryMetadataFieldValues == null)
            {
                categoryMetadataFieldValues = new HashSet<>();
            }
            categoryMetadataFieldValues.add(value);
            //this.setCategoryMetadataFieldValues(categoryMetadataFieldValues);
            value.setCategory(this);
        }
    }

    public void addProducts(Product product)
    {
        if(product!=null)
        {
            if(productList==null)
            {
                productList = new ArrayList<>();
            }
            productList.add(product);
        }
    }

    public Category(String name) {
        this.name = name;
    }

    public Category() {
    }

}
