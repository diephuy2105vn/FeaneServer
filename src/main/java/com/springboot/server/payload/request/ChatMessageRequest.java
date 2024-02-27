package com.springboot.server.payload.request;

import com.springboot.server.payload.constants.EMessageType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatMessageRequest {
    private Long id;
    private String sender;
    private String content;
    private EMessageType type;
}
