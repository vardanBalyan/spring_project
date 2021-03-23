package com.ttn.bootcampProject.entities.products.categories;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMetadataFieldValuesId implements Serializable {

    private int categoryId;
    private int categoryMetadataFieldId;
}
