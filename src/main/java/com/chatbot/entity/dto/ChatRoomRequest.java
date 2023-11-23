package com.chatbot.entity.dto;

import com.chatbot.domain.ChatRoomMessage;
import com.chatbot.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Data
@Builder
public class ChatRoomRequest {

    private String chatRoomName;
    private String chatRoomDescription;
    private User createdBy;
    private List<User> participantList;
}
