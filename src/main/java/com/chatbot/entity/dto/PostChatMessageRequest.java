package com.chatbot.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostChatMessageRequest {

    private String chatRoomName;
    private Long userId;
    private Long toUserId;
    private String message;
}
