package com.ttn.bootcampProject.entities.products;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewId implements Serializable {

    private int customerUserId;
    private int productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductReviewId that = (ProductReviewId) o;
        return customerUserId == that.customerUserId && productId == that.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerUserId, productId);
    }
}
