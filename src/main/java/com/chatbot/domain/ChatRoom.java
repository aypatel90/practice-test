package com.chatbot.domain;

import com.chatbot.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    private final long chatRoomId = ThreadLocalRandom.current().nextLong(100000);
    private String chatRoomName;
    private String chatRoomDescription;
    private User createdBy;
    private User updatedBy;
    private Date createdDate;
    private Date updatedDate;
    private boolean isActive;
    private List<User> participantList;
    private Set<ChatRoomMessage> chatRoomMessages;
}
