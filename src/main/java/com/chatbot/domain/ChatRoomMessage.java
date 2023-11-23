package com.chatbot.domain;

import com.chatbot.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Data
@Builder
public class ChatRoomMessage {

    private String chatRoomName;
    private String message;
    private User createdBy;
    private User toUser;
    private Date createdDate;
    private Date updatedDate;
    private boolean isActive;
}
