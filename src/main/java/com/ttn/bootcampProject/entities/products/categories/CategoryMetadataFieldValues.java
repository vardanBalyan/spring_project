package com.ttn.bootcampProject.entities.products.categories;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="category_metadata_field_values")
@Getter
@Setter
public class CategoryMetadataFieldValues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String value;
}
