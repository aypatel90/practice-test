package com.chatbot.entity.dto;

import com.chatbot.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomRequest {

    @NotBlank
    @Size(min = 3, max = 60)
    private String chatRoomName;
    private String chatRoomDescription;
    private User createdBy;
    private List<User> participantList;
}
