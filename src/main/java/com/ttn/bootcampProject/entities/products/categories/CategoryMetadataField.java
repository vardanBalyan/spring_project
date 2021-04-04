package com.ttn.bootcampProject.entities.products.categories;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "categoryMetadataField")
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
            value.setCategoryMetadataField(this);
        }
    }

}
