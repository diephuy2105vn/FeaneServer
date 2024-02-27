package com.springboot.server.payload.response;

import com.springboot.server.models.Cart;
import com.springboot.server.models.CartDetail;
import com.springboot.server.models.Product;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor

public class CartDetailResponse {
    private Long id;
    private ProductResponse product;
    private int quantity;
    private long totalPrice;
    private String createdAt;

    public CartDetailResponse (CartDetail cartDetail) {
        id = cartDetail.getId();
        product =  new ProductResponse(cartDetail.getProduct());
        quantity = cartDetail.getQuantity();
        totalPrice = cartDetail.getTotalPrice();
        createdAt = cartDetail.getCreatedAt().toString();
    }
}
