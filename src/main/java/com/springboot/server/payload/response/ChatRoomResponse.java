package com.springboot.server.payload.response;

import com.springboot.server.models.ChatRoom;
import com.springboot.server.models.User;
import com.springboot.server.service.UserDetailsImpl;
import jakarta.persistence.OrderBy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
public class ChatRoomResponse {
    Long id;
    UserResponse sender;
    Set<UserResponse> receivers;
    @OrderBy("createdAt ASC")
    List<ChatMessageResponse> messages;

    public ChatRoomResponse(ChatRoom chatRoom, User userLogged) {
        Set<UserResponse> receivers = chatRoom.getUsers().stream()
                .filter(item -> item != userLogged)
                .map(item -> new UserResponse(item.getId(), item.getUsername(), item.getName(), item.getAvatar()))
                .collect(Collectors.toSet());
        List<ChatMessageResponse> chatMessageResponses = chatRoom.getMessages().stream()
                .map(item -> new ChatMessageResponse(item)).collect(Collectors.toList());
        this.id = chatRoom.getId();
        this.sender = new UserResponse(userLogged.getId(), userLogged.getUsername(), userLogged.getName(), userLogged.getAvatar());
        this.receivers = receivers;
        this.messages = chatMessageResponses;
    }
}
