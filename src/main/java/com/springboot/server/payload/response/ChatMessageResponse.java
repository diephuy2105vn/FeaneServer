package com.springboot.server.payload.response;

import com.springboot.server.models.ChatMessage;
import com.springboot.server.payload.constants.EMessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private Long chatRoomId;
    private String sender;
    private String content;
    private EMessageType type;
    private Date createdAt;

    public ChatMessageResponse(ChatMessage chatMessage) {
        this.id = chatMessage.getId();
        this.chatRoomId = chatMessage.getChatRoom().getId();
        this.sender = chatMessage.getSender().getUsername();
        this.content = chatMessage.getContent();
        this.type = chatMessage.getType();
        this.createdAt = chatMessage.getCreatedAt();
    }
}
