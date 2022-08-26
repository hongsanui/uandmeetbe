package com.project.uandmeetbe.chat;

import com.project.uandmeetbe.chat.auditing.ChatBaseEntity;
import com.project.uandmeetbe.room.Room;
import com.project.uandmeetbe.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * <h1>ChatMessage</h1>
 * <p>
 *     채팅 엔티티
 * </p>
 */
@Getter
@Entity
public class ChatMessage extends ChatBaseEntity {

    public ChatMessage(){
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MESSAGE")
    private String message;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    @Builder(access = AccessLevel.PRIVATE)
    private ChatMessage(String message, User user, Room room){
        this.message = message;
        this.user = user;
        this.room = room;
    }

    public static ChatMessage of(String message, User user, Room room){
        return ChatMessage.builder()
                .message(message)
                .user(user)
                .room(room)
                .build();
    }

}
