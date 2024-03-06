package com.springboot.server.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OrderDetailRequest {
    private long shopId;
    private List<CartDetailRequest> cartDetails;
}
