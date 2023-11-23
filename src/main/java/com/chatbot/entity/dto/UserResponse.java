package com.chatbot.entity.dto;

import com.chatbot.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long userId;
    private String firstname;
    private String lastname;
    private String email;
    private boolean isEnabled;
    private Role role;
}
