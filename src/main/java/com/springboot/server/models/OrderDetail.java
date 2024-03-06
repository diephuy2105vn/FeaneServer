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
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderTb orderTb;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private Long totalPrice;

    @CreationTimestamp
    private Date createdAt;

    public OrderDetail (OrderTb orderTb, Product product, int quantity) {
        this.orderTb = orderTb;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = product.getPrice()*quantity;
    }
}
