package com.chatbot.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chatroom")
public class Chatroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "chatroom_name", nullable = false)
    private String chatroomName;

    @Column(name = "chatroom_description")
    private String chatroomDescription;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    private Timestamp createdDate;

    @CreatedBy
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    /*@ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="chatroom_messages",
    joinColumns = @JoinColumn(name = "id"), inverseJoinColumns = @JoinColumn(name = "id"))
    private Set<ChatroomMessages> chatroomMessagesSet;*/

    /*@ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="chatroom_users",
            joinColumns = @JoinColumn(name = "id"), inverseJoinColumns = @JoinColumn(name = "id"))
    private Set<ChatroomUsers> chatroomUsersSet;*/

    @OneToMany(mappedBy = "chatroom")
    Set<ChatroomUsers> chatroomUsersSet;

    @OneToMany(mappedBy = "chatroomMessage")
    Set<ChatroomMessages> chatroomMessagesSet;

    public Chatroom(Long id) {
        this.id = id;
    }

}
