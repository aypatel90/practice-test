package com.chatbot.controller;

import com.chatbot.entity.dto.AppResponse;
import com.chatbot.entity.dto.AuthenticateRequest;
import com.chatbot.entity.dto.AuthenticationResponse;
import com.chatbot.entity.dto.RegisterRequest;
import com.chatbot.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
        var registry = authenticationService.register(request);
        return new ResponseEntity<>(registry, HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticateRequest request) {
        var authentication = authenticationService.authenticate(request);
        return new ResponseEntity<>(authentication, HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ResponseEntity<AppResponse> confirmUser(@RequestParam("token") String token) {
        boolean isSuccess = authenticationService.verifyToken(token);
        AppResponse response = AppResponse.builder()
                .responseCode("200")
                .responseMessage("A sua conta foi verificada com sucesso!!!")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/refresh-token", produces = "application/json")
    public void refreshToken(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        authenticationService.refreshToken(token, response);
    }

    @GetMapping(value = "/test", produces = "application/json")
    public void refreshToken(HttpServletResponse response) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("test response");
        new ObjectMapper().writeValue(
                response.getOutputStream(), builder.toString()
        );
    }
}
