package com.springboot.server.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "delivery_addresses")
@NoArgsConstructor
public class DeliveryAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phoneNumber;
    private String address;
    private Boolean isDefault;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    public DeliveryAddress(String name, String phoneNumber,String address, Boolean isDefault) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isDefault = isDefault;
    }
}
