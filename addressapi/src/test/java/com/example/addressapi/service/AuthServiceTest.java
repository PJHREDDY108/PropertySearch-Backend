package com.example.addressapi.service;

import com.example.addressapi.dto.AuthResponse;
import com.example.addressapi.dto.LoginRequest;
import com.example.addressapi.dto.RegisterRequest;
import com.example.addressapi.entity.User;
import com.example.addressapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_whenUsernameIsFree_createsUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("alice");
        request.setPassword("plainPassword");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");

        AuthResponse response = authService.register(request);

        assertThat(response.getUsername()).isEqualTo("alice");
        assertThat(response.getMessage()).isEqualTo("Registered successfully");

        verify(userRepository).save(argThat(u ->
                u.getUsername().equals("alice") && u.getPassword().equals("hashedPassword")));
    }

    @Test
    void register_whenUsernameAlreadyExists_throwsIllegalArgumentException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("bob");
        request.setPassword("pw");

        when(userRepository.findByUsername("bob"))
                .thenReturn(Optional.of(new User(1L, "bob", "hashed")));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_withCorrectCredentials_returnsSuccessResponse() {
        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("plainPassword");

        User storedUser = new User(1L, "alice", "hashedPassword");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("plainPassword", "hashedPassword")).thenReturn(true);

        AuthResponse response = authService.login(request);

        assertThat(response.getMessage()).isEqualTo("Login successful");
    }

    @Test
    void login_withWrongPassword_throwsBadCredentialsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("wrongPassword");

        User storedUser = new User(1L, "alice", "hashedPassword");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_withUnknownUsername_throwsBadCredentialsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("ghost");
        request.setPassword("whatever");

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid username or password");
    }
}