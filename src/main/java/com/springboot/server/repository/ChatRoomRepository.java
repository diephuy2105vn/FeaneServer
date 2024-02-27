package com.springboot.server.repository;

import com.springboot.server.models.ChatRoom;
import com.springboot.server.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT c FROM ChatRoom c JOIN c.users u WHERE u.username = :username")
    Set<ChatRoom> findAllByUsername(@Param("username") String username);
    @Query("SELECT c \n" +
            "FROM ChatRoom c \n" +
            "JOIN c.users u \n" +
            "WHERE u.username IN :usernames \n" +
            "GROUP BY c.id \n" +
            "HAVING COUNT(*) = :size \n")
   ChatRoom findByUsernames(@Param("usernames") Set<String> usernames, @Param("size") int size);
}
