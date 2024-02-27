package com.springboot.server.payload.response;

import com.springboot.server.models.Shop;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private UserResponse owner;
    private Set<String> productTypes;
    private Date createdAt;

    public ShopResponse (Shop shop) {
        id = shop.getId();
        name= shop.getName();
        description = shop.getDescription();
        address = shop.getAddress();
        createdAt= shop.getCreatedAt();
        owner = new UserResponse(shop.getOwner());
        String strProductTypes = shop.getProductTypes();
        strProductTypes = strProductTypes.substring(1, strProductTypes.length()-1);
        String[] arrayTypes = strProductTypes.split(", ");
        productTypes = new HashSet<>(Arrays.asList(arrayTypes));
    }
}
