package com.springboot.server.payload.response;

import jakarta.persistence.OrderBy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String username;
    private String name;
    private String address;
    private String phoneNumber;
    private String avatar;
    @OrderBy("createdAt ASC")
    private List<ShopResponse> shops;
    private List<String> roles;
    private String accessToken;
    private String refreshToken;
}
