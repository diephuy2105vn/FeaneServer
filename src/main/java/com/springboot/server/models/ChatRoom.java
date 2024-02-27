package com.springboot.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "chatroom")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "chatroom_user",
            joinColumns = @JoinColumn(name = "chatroom_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new HashSet<>();
    @OneToMany(mappedBy = "chatRoom")
    @OrderBy("createdAt ASC")
    private Set<ChatMessage> messages = new HashSet<>();

    public boolean hasUser(User user) {
        List<String> usernames = users.stream().map(User::getUsername).collect(Collectors.toList());
        return usernames.contains(user.getUsername());
    }
}
