package com.springboot.server.payload.response;

import com.springboot.server.models.Image;
import com.springboot.server.models.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String type;
    private int quantity;
    private int sold;
    private long price;
    private String note;
    private ShopResponse shop;
    private List<String> images;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.type = product.getType();
        this.quantity = product.getQuantity();
        this.sold = product.getSold();
        this.price = product.getPrice();
        this.note = product.getNote();
        this.shop = new ShopResponse(product.getShop());
        this.images = product.getImages().stream()
                .map(Image::getUrl)
                .collect(Collectors.toList());

    }
}
