package com.springboot.server.controller;

import com.springboot.server.models.ChatRoom;
import com.springboot.server.models.User;
import com.springboot.server.payload.exception.ResourceNotFoundException;
import com.springboot.server.payload.response.ChatMessageResponse;
import com.springboot.server.payload.response.ChatRoomResponse;
import com.springboot.server.payload.response.EMessageResponse;
import com.springboot.server.payload.response.MessageResponse;
import com.springboot.server.repository.ChatMessageRepository;
import com.springboot.server.repository.ChatRoomRepository;
import com.springboot.server.repository.UserRepository;
import com.springboot.server.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/room")
@PreAuthorize("hasRole('USER')")
public class RoomController {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private UserRepository userRepository;
    private UserDetailsImpl getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl)authentication.getPrincipal();
    };
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll () {
        try {
            UserDetailsImpl userDetails = getUserDetails();
            User userLogged = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found"));
            List<ChatRoomResponse> chatRoomResponses = chatRoomRepository.findAllByUsername(userDetails.getUsername()).stream()
                    .map(item -> new ChatRoomResponse(item, userLogged))
                    .collect(Collectors.toList());
            if(chatRoomResponses.size() > 2) {
                chatRoomResponses.sort((chatRoom1, chatRoom2) -> {
                    if(chatRoom1.getMessages().size() > 0 && chatRoom2.getMessages().size() > 0) {
                        ChatMessageResponse lastMessage1 = chatRoom1.getMessages().get(chatRoom1.getMessages().size() - 1);
                        ChatMessageResponse lastMessage2 = chatRoom2.getMessages().get(chatRoom2.getMessages().size() - 1);
                        return lastMessage2.getCreatedAt().compareTo(lastMessage1.getCreatedAt());
                    }
                    else if (chatRoom1.getMessages().size() > 0) {
                        return -1; // chatRoom1 có tin nhắn, đặt nó trước chatRoom2
                    } else if (chatRoom2.getMessages().size() > 0) {
                        return 1; // chatRoom2 có tin nhắn, đặt nó trước chatRoom1
                    }
                    return 0;
                });
            }
            return ResponseEntity.ok().body(chatRoomResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }

    @GetMapping()
    public ResponseEntity<?> getRoomWithUsername (@RequestParam String username) {
        try {
            UserDetailsImpl userDetails = getUserDetails();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(()-> new ResourceNotFoundException("Not found with user"));
            User userLogged = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(()-> new ResourceNotFoundException("Not found with user"));

            Set<User> users = new HashSet<>();
            users.add(user);
            users.add(userLogged);
            if(user.getUsername() == userLogged.getUsername()) {
                throw new Exception("Bad Request");
            }
            ChatRoom chatRoom = chatRoomRepository.findByUsernames(
                    users.stream().map(item -> item.getUsername()).collect(Collectors.toSet()), 2);

            if(chatRoom != null ) {
                ChatRoomResponse chatRoomResponse = new ChatRoomResponse(chatRoom, userLogged);
                return ResponseEntity.ok().body(chatRoomResponse);
            } else {
                ChatRoom chatRoomNew = new ChatRoom();
                chatRoomNew.setUsers(users);
                ChatRoom chatRoomSaved = chatRoomRepository.save(chatRoomNew);
                ChatRoomResponse chatRoomResponse = new ChatRoomResponse(chatRoomNew, userLogged);
                return ResponseEntity.ok().body(chatRoomResponse);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomWithRoomId (@PathVariable String id) {
        try {
            Long roomId = Long.parseLong(id);
            UserDetailsImpl userDetails = getUserDetails();
            User userLogged = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found"));
            ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

            ChatRoomResponse chatRoomResponse = new ChatRoomResponse(chatRoom, userLogged);
            chatRoomResponse.getMessages().forEach(
                    item -> {
                        System.out.println(item.getCreatedAt());
                    }
            );
            return ResponseEntity.ok().body(chatRoomResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody String username ) {
        try {
            UserDetailsImpl userDetails = getUserDetails();
            ChatRoom room = new ChatRoom();
            Set<User> users = new HashSet<>();

            users.add(userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User Details not found")));
            users.add(userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Username not found")));
            room.setUsers(users);

            chatRoomRepository.save(room);

            return ResponseEntity.ok().body(new MessageResponse(EMessageResponse.MESSAGE_SUCCESS, "Create successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }

}
