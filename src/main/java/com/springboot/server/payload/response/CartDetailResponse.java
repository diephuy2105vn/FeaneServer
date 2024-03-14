package com.springboot.server.payload.response;


import com.springboot.server.models.CartDetail;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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
