package com.chatbot.entity.dto;

import com.chatbot.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AddChatRoomUserRequest {

    private String chatRoomName;
    private Long userId;
}
