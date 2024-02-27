package com.springboot.server.payload.response;

import com.springboot.server.models.Cart;
import com.springboot.server.models.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
public class CartResponse {
    private Long id;
    private UserResponse user;
    private List<CartDetailResponse> cartDetails;
    public CartResponse (Cart cart) {
        id = cart.getId();
        user = new UserResponse(cart.getUser());
        cartDetails = cart.getCartDetails().stream().map(CartDetailResponse::new).collect(Collectors.toList());
    }
}
