package com.springboot.server.payload.request;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class SignupRequest {
    private String username;
    private String password;
    private String name;
    private String address;
    private String phoneNumber;
    private Set<String> role;
}
