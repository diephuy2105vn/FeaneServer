package com.springboot.server.payload.response;

import com.springboot.server.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String name;
    private String avatar;

    public UserResponse (User user) {
        id = user.getId();
        username = user.getUsername();
        name = user.getName();
        avatar = user.getAvatar();
    }
}
