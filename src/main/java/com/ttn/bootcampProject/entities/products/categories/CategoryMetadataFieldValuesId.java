package com.ttn.bootcampProject.entities.products.categories;

import lombok.*;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMetadataFieldValuesId implements Serializable {

    @NotNull
    private long categoryId;
    @NotNull
    private long categoryMetadataFieldId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryMetadataFieldValuesId that = (CategoryMetadataFieldValuesId) o;
        return categoryId == that.categoryId && categoryMetadataFieldId == that.categoryMetadataFieldId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, categoryMetadataFieldId);
    }
}
