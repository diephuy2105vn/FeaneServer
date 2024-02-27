package com.springboot.server.models;

import com.springboot.server.payload.constants.EProductType;
import com.springboot.server.payload.request.ShopRegisterRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String address;
    private String productTypes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;
    @CreationTimestamp
    private Date createdAt;
    @OneToMany(mappedBy = "shop")
    @OrderBy("createdAt")
    private List<Product> products;
    public Shop (ShopRegisterRequest shopRegisterRequest, User owner) {
        this.name = shopRegisterRequest.getName();
        this.description = shopRegisterRequest.getDescription();
        this.address = shopRegisterRequest.getAddress();
        Set<String> strProductTypes = shopRegisterRequest.getProductTypes();
        Set<EProductType> productTypes = new HashSet<>();
        if (!strProductTypes.isEmpty()) {
            strProductTypes.forEach(type -> {
                switch (type) {
                    case "CLOTHES":
                       productTypes.add(EProductType.CLOTHES);
                        break;
                    case "SHOES":
                        productTypes.add(EProductType.SHOES);
                        break;
                    case "ACCESSORIES":
                        productTypes.add(EProductType.ACCESSORIES);
                        break;
                    default:
                        productTypes.add(EProductType.OTHER);
                        break;
                }
            });
        }
        this.productTypes = productTypes.toString();
        this.owner = owner;
    }
}
