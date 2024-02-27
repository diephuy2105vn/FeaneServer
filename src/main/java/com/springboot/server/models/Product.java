package com.springboot.server.models;


import com.springboot.server.payload.constants.EProductType;
import com.springboot.server.payload.request.ProductRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(columnDefinition = "VARCHAR(10000)")
    private String description;
    private String type;
    private int quantity;
    private int sold = 0;
    private long price;
    private String note;
    @OneToMany(mappedBy = "product")
    private List<Image> images;

    @OneToOne(mappedBy = "product")
    private CartDetail cartDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    public Product (ProductRequest productRequest) {
        this.name = productRequest.getName();
        this.description = productRequest.getDescription();
        this.type = productRequest.getType().toString();
        this.quantity = productRequest.getQuantity();
        this.price = productRequest.getPrice();
        this.note = productRequest.getNote();
    }
}
