package com.chatbot.service;

import com.chatbot.entity.User;
import com.chatbot.entity.dto.*;
import com.chatbot.exception.BusinessException;
import com.chatbot.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
             new EntityNotFoundException("Entity Not Found !"));
    }

    public AppResponse updateUser(RegisterRequest request, Long userId) {
        User user = getUserById(userId);
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setUserName(request.getUserName());
        userRepository.save(user);

        return AppResponse.builder()
                .responseCode("200")
                .responseMessage("Perfil actualizado com sucesso!")
                .name(request.getFirstname())
                .build();
    }

    public AppResponse changePassword(ChangePasswordRequest request, Principal authenticatedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) authenticatedUser).getPrincipal();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("Palavra-passe Inválida!");
        }

        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new BusinessException("Palavra-passe não é igual!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return AppResponse.builder()
                .responseCode("200")
                .responseMessage("Palavra-passe foi actualizada com sucesso!")
                .name(user.getFirstname())
                .build();
    }

    public Integer countAllEnabledUsers() {
        return userRepository.countAllEnabledUser();
    }

    public UserResponse maptoUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .isEnabled(user.isEnabled())
                //.role(user.getRole())
                .build();
    }
}
