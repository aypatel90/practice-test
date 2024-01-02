package com.chatbot.domain;

import com.chatbot.entity.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMessage {

    private String chatRoomName;
    private String message;
    private UserResponse createdBy;
    private UserResponse toUser;
    private Date createdDate;
    private Date updatedDate;
    private boolean isActive;
}
