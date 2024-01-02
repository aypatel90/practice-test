package com.chatbot.entity.dto;

import com.chatbot.domain.ChatRoomMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllChatMessageResponse {

    @JsonProperty("chat_message_list")
    private Set<ChatRoomMessage> chatMessageSet;
    @JsonProperty("chatroom_name")
    private String chatRoomName;
}
