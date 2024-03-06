package com.springboot.server.payload.response;

import com.springboot.server.models.OrderDetail;
import com.springboot.server.models.OrderTb;
import com.springboot.server.models.Shop;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private ShopResponse shop;
    private Date createdAt;
    private Long totalPrice;
    private List<OrderDetailResponse> orderDetails;
    private String status;

    public OrderResponse (OrderTb orderTb) {
        this.id = orderTb.getId();
        this.name = orderTb.getName();
        this.address= orderTb.getAddress();
        this.phoneNumber= orderTb.getPhoneNumber();
        this.shop = new ShopResponse(orderTb.getShop());
        this.totalPrice = orderTb.getTotalPrice();
        this.createdAt = orderTb.getCreatedAt();
        this.orderDetails = orderTb.getOrderDetails().stream().map(OrderDetailResponse::new).toList();
        this.status = orderTb.getStatus();
    }
}
