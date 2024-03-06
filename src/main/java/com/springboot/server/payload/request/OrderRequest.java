package com.springboot.server.payload.request;

import com.springboot.server.models.OrderDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class OrderRequest {
    private String name;
    private String address;
    private String phoneNumber;
    private List<OrderDetailRequest> details;
}
