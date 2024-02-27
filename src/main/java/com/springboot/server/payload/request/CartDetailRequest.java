package com.springboot.server.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CartDetailRequest {
    private Long productId;
    private int quantity;
}
