package com.springboot.server.payload.response;

import com.springboot.server.models.ShopCart;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ShopCartResponse {
    private ShopResponse shop;
    private List<CartDetailResponse> details;

    public ShopCartResponse(ShopCart shopCart) {
        shop = new ShopResponse(shopCart.getShop());
        details = shopCart.getCartDetails().stream().map(CartDetailResponse::new).collect(Collectors.toList());
    }
}
