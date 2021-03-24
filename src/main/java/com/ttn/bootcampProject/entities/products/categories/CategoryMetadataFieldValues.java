package com.ttn.bootcampProject.entities.products.categories;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="category_metadata_field_values")
@Getter
@Setter
public class CategoryMetadataFieldValues {

    @EmbeddedId
    CategoryMetadataFieldValuesId id;

    private String value;


    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    Category category;


    @ManyToOne
    @MapsId("categoryMetadataFieldId")
    @JoinColumn(name = "category_metadata_field_id")
    CategoryMetadataField categoryMetadataField;
}
