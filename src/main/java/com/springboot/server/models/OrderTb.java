package com.springboot.server.models;

import com.springboot.server.payload.request.OrderRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@Entity
public class OrderTb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @CreationTimestamp
    private Date createdAt;

    private Long totalPrice;

    @OneToMany(mappedBy = "orderTb")
    @OrderBy("createdAt asc")
    private List<OrderDetail> orderDetails;

    private String status = "UNCONFIRM";


    public OrderTb (OrderRequest orderRequest) {
        this.name = orderRequest.getName();
        this.address= orderRequest.getAddress();
        this.phoneNumber = orderRequest.getPhoneNumber();
    }
}
