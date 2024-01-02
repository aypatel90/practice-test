package com.chatbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.sql.Timestamp;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "created_date", nullable = false)
    private Timestamp createdDate;

    @Column(name = "updated_date", nullable = false)
    private Timestamp updatedDate;

    @CreatedBy
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @LastModifiedBy
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "updated_by", nullable = true)
    private User updatedBy;

    @Column(name = "to_user", nullable = true)
    private Long toUser;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @OneToMany(mappedBy = "chatMessage")
    Set<ChatroomMessages> chatroomMessagesSet;

   /* @ManyToMany(cascade = CascadeType.ALL)
    private Set<ChatroomUsers> chatroomUsersCollection;*/

    public ChatMessage(Long id) {
        this.id = id;
    }

}
