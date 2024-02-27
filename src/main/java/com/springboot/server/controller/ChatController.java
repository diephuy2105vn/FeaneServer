package com.springboot.server.controller;

import com.springboot.server.models.ChatMessage;
import com.springboot.server.models.ChatRoom;
import com.springboot.server.payload.constants.EMessageType;
import com.springboot.server.models.User;
import com.springboot.server.payload.exception.ResourceNotFoundException;
import com.springboot.server.payload.request.ChatMessageRequest;
import com.springboot.server.payload.response.ChatMessageResponse;
import com.springboot.server.repository.ChatMessageRepository;
import com.springboot.server.repository.ChatRoomRepository;
import com.springboot.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;

import java.util.logging.Logger;
import java.util.stream.Collectors;


@Controller
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private UserRepository userRepository;
    public static final Logger LOG
            = Logger.getLogger(String.valueOf(ChatController.class));
    @MessageMapping("/chat/{roomId}/sendMessage")
    @SendTo("/topic/room/{roomId}")
    public ChatMessageResponse sendMessage(@DestinationVariable String roomId, @Payload ChatMessageRequest message) {
        try {
            User user = userRepository.findByUsername(message.getSender())
                    .orElseThrow(() -> new UsernameNotFoundException("Not found sender"));
            ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(roomId))
                    .orElseThrow(() -> new ResourceNotFoundException("Not found room"));
            String users = chatRoom.getUsers().stream()
                    .map(User::getUsername) // Chuyển đổi User thành tên người dùng
                    .collect(Collectors.joining(", "));
            if(!chatRoom.hasUser(user)) {
                throw new ResourceNotFoundException("Sender do not have in room");
            }
            if(message.getType() == EMessageType.CHAT) {
                ChatMessage chatMessage = new ChatMessage(user, chatRoom, message.getContent(), message.getType());
                ChatMessage chatMessageSaved = chatMessageRepository.save(chatMessage);
                ChatMessageResponse chatMessageResponse =new ChatMessageResponse(chatMessageSaved);
                return chatMessageResponse;
            }
            else if (message.getType() == EMessageType.EDIT) {
               ChatMessage chatMessageSaved = chatMessageRepository.findByIdAndSender(message.getId(), user)
                        .map(item -> {
                            item.setType(message.getType());
                            item.setContent(message.getContent());
                            return chatMessageRepository.save(item);
                        })
                        .orElseThrow(() -> new ResourceNotFoundException("Not found chat message"));
               ChatMessageResponse chatMessageResponse =new ChatMessageResponse(chatMessageSaved);
               chatMessageResponse.setType(message.getType());
               return chatMessageResponse;
            }
            else {
                chatMessageRepository.findByIdAndSender(message.getId(), user)
                        .map((item) -> {
                            chatMessageRepository.deleteById(item.getId());
                            return true;
                        })
                        .orElseThrow(() -> new ResourceNotFoundException("Not found chat message"));
                ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
                chatMessageResponse.setId(message.getId());
                chatMessageResponse.setType(message.getType());
                return chatMessageResponse;
            }

        }
        catch (Exception e) {
            LOG.warning(e.getMessage());
            return null;
        }
    }

//    @MessageMapping("/chat/addUser")
//    @SendTo("/topic/public")
//    public ChatMessage addUser(@Payload ChatMessage chatMessage,
//                               SimpMessageHeaderAccessor headerAccessor) {
//        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
//        return chatMessage;
//    }
}

