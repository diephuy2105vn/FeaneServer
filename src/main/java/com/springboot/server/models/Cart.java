package com.springboot.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Comparator;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart")
    private List<ShopCart> shopCarts;

    public Cart (User user) {
        this.user = user;
    }
    public void sortShopCarts() {
        shopCarts.sort(Comparator.comparing(s -> s.getCartDetails().get(0).getCreatedAt()));
    }

}
