package com.ttn.bootcampProject.entities.orders;

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
public class CartId implements Serializable {

    private long customerUserId;
    private long productVariationId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartId cartId = (CartId) o;
        return customerUserId == cartId.customerUserId && productVariationId == cartId.productVariationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerUserId, productVariationId);
    }
}
