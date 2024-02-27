package com.springboot.server.models;


import com.springboot.server.payload.constants.EMessageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "chatmessage")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;

    private String content;

    private EMessageType type;

    @CreationTimestamp
    private Date createdAt;

    public ChatMessage (User sender, ChatRoom chatRoom, String content, EMessageType type) {
        this.sender = sender;
        this.chatRoom = chatRoom;
        this.content = content;
        this.type = type;
    }

}

