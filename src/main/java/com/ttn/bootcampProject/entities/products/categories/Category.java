package com.ttn.bootcampProject.entities.products.categories;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_category_id")
    private Set<Category> categorySet;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Set<CategoryMetadataFieldValues> categoryMetadataFieldValues;

    public void addCategoryMetadataFieldValues(CategoryMetadataFieldValues value)
    {
        if(value!=null)
        {
            if(categoryMetadataFieldValues == null)
            {
                categoryMetadataFieldValues = new HashSet<>();
            }
            categoryMetadataFieldValues.add(value);
        }
    }

    public Category(String name) {
        this.name = name;
    }

    public Category() {
    }

}
