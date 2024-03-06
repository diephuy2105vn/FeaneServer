package com.springboot.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "cartDetail")
public class CartDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopCart_id")
    private ShopCart shopCart;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    private int quantity;

    @CreationTimestamp
    private Date createdAt;

    public CartDetail(ShopCart shopCart, Product product, int quantity) {
        this.shopCart = shopCart;
        this.product = product;
        this.quantity = quantity;
    }
    public long getTotalPrice() {
        return product.getPrice() * quantity;
    }
}
