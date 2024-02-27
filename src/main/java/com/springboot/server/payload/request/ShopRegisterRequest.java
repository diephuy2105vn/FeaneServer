package com.springboot.server.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ShopRegisterRequest {
    private String name;
    private String description;
    private String address;
    private Set<String> productTypes;
}
