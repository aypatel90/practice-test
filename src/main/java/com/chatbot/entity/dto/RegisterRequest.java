package com.chatbot.entity.dto;

import com.chatbot.entity.Role;
import jakarta.validation.constraints.Email;
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
public class RegisterRequest {

    @NotBlank
    @Size(min = 3, max = 60)
    private String firstname;

    @NotBlank
    @Size(min = 3, max = 30)
    private String lastname;

    @NotBlank
    @Size(min = 3, max = 30)
    private String userName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 4, max = 20)
    private String password;

    private Role role;

}
