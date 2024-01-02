package com.chatbot.service;

import com.chatbot.entity.ConfirmationToken;
import com.chatbot.entity.Role;
import com.chatbot.entity.User;
import com.chatbot.entity.dto.AuthenticateRequest;
import com.chatbot.entity.dto.AuthenticationResponse;
import com.chatbot.entity.dto.RegisterRequest;
import com.chatbot.entity.dto.UserResponse;
import com.chatbot.exception.BusinessException;
import com.chatbot.exception.EntityNotFoundException;
import com.chatbot.repository.ConfirmationTokenRepository;
import com.chatbot.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenRepository tokenRepository;

    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("User email is already been used. Please try with different email.");
        }

        User existingUser = userRepository.findUserByUserName(request.getUserName());

        if(existingUser != null) {
            throw new BusinessException("User Name is already been used. Please try with different username.");
        }

        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .userName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .build();
        var savedUser = userRepository.save(user);
        var token = jwtTokenService.generateToken(user);
        saveUserToken(savedUser, token);

        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken("refreshToken")
                .user(maptoUserResponse(user))
                .build();
    }

    public User processingRegistration(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("User email is already been used. Please try with different email. " +request.getEmail());
        }

        User existingUser = userRepository.findUserByUserName(request.getUserName());

        if(existingUser != null) {
            throw new BusinessException("User Name is already been used. Please try with different username. "+request.getUserName());
        }

        return User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .userName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .build();
    }

    public AuthenticationResponse writingRegistration(Chunk<? extends User> userList) {
       for(User user : userList) {
           var savedUser = userRepository.save(user);
           var token = jwtTokenService.generateToken(savedUser);
           saveUserToken(savedUser, token);
       }

        return AuthenticationResponse.builder()
                .accessToken("")
                .refreshToken("refreshToken")
                .build();
    }

    public Boolean verifyToken(String token) {
        ConfirmationToken confirmationToken = tokenRepository.findByToken(token).orElseThrow(() ->
                new BusinessException("Token não foi encontrado"));
        User user = userRepository.findByEmail(confirmationToken.getUser().getEmail()).orElseThrow(() ->
                new EntityNotFoundException("Usuário não foi encontrado"));
        user.setEnabled(true);
        userRepository.save(user);
        return true;
    }

    public AuthenticationResponse authenticate(AuthenticateRequest authenticateRequest) {
        var user = userRepository.findByEmail(authenticateRequest
                .getEmail()).orElseThrow();
        revokeAllUserTokens(user);
        var token = jwtTokenService.generateToken(user);
        var refreshToken = jwtTokenService.generateRefreshToken(user);
        saveUserToken(user, token);

        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .user(maptoUserResponse(user))
                .build();
    }

    public void refreshToken(String token, HttpServletResponse response) throws IOException {
        final String userEmail;

        try {
            userEmail = jwtTokenService.extractUserEmail(token);
        } catch (ExpiredJwtException exp) {
            throw new BusinessException("Token is expired. Please do login again.");
        }
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail).orElseThrow();
            if (jwtTokenService.isTokenValid(token, user)) {
                var accessToken = jwtTokenService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .user(maptoUserResponse(user))
                        .accessToken(accessToken)
                        .refreshToken(token)
                        .build();
                new ObjectMapper().writeValue(
                        response.getOutputStream(), authResponse
                );
            }
        }
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = ConfirmationToken.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .createdDate(LocalDateTime.now())
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validTokens.isEmpty()) {
            return;
        }
        validTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validTokens);
    }

    private UserResponse maptoUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .isEnabled(user.isEnabled())
                .role(user.getRole())
                .build();
    }
}
