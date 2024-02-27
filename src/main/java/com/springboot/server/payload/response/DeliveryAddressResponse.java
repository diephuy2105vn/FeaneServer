package com.springboot.server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeliveryAddressResponse {
    private Long id;
    private String name;
    private String phoneNumber;
    private String address;
    private String isDefault;

    public DeliveryAddressResponse(Long id,String name, String phoneNumber, String address, Boolean isDefault) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isDefault = isDefault? "true": "false";
    }
}
