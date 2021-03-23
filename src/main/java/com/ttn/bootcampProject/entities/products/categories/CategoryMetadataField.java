package com.ttn.bootcampProject.entities.products.categories;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class CategoryMetadataField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_metadata_filed_id")
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

}
