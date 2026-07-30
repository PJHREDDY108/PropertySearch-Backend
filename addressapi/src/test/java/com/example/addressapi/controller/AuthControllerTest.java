package com.example.addressapi.controller;

import com.example.addressapi.dto.AuthResponse;
import com.example.addressapi.dto.LoginRequest;
import com.example.addressapi.dto.RegisterRequest;
import com.example.addressapi.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_whenSuccessful_returns200() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("alice");
        request.setPassword("secret123");

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new AuthResponse("alice", "Registered successfully"));

        ResponseEntity<?> response = authController.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void register_whenUsernameTaken_returns409() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("bob");
        request.setPassword("secret123");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Username already exists"));

        ResponseEntity<?> response = authController.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void login_whenSuccessful_returns200() {
        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("secret123");

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new AuthResponse("alice", "Login successful"));

        ResponseEntity<?> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void login_whenInvalidCredentials_returns401() {
        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("wrongPassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        ResponseEntity<?> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}