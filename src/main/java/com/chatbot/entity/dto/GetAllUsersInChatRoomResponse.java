package com.chatbot.entity.dto;

import com.chatbot.domain.ChatRoomMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllUsersInChatRoomResponse {

    @JsonProperty("user_list")
    private List<UserResponse> userResponseList;
    @JsonProperty("chatroom_name")
    private String chatRoomName;
}
