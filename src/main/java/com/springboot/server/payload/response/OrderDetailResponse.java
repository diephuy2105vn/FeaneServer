package com.springboot.server.payload.response;


import com.springboot.server.models.CartDetail;

import com.springboot.server.models.OrderDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor

public class OrderDetailResponse {
    private Long id;
    private ProductResponse product;
    private int quantity;
    private long totalPrice;
    private String createdAt;

    public OrderDetailResponse (OrderDetail orderDetail) {
        id = orderDetail.getId();
        product =  new ProductResponse(orderDetail.getProduct());
        quantity = orderDetail.getQuantity();
        totalPrice = orderDetail.getTotalPrice();
        createdAt = orderDetail.getCreatedAt().toString();
    }
}

