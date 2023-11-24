package com.chatbot.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddChatRoomUserRequest {

    @NotBlank
    @Size(min = 3, max = 60)
    private String chatRoomName;
    private Long userId;
}
