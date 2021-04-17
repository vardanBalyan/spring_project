package com.ttn.bootcampProject.entities.orders;

import com.ttn.bootcampProject.entities.products.ProductVariation;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Map;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@TypeDef(name = "jsonb", typeClass = JsonStringType.class)
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private int quantity;
    private double price;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String,String> productVariationMetadata;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders orders;
    @ManyToOne
    @JoinColumn(name = "product_variation_id")
    private ProductVariation productVariation;
}
