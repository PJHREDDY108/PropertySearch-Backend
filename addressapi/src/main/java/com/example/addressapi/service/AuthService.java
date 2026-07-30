package com.example.addressapi.service;

import com.example.addressapi.dto.AuthResponse;
import com.example.addressapi.dto.LoginRequest;
import com.example.addressapi.dto.RegisterRequest;
import com.example.addressapi.entity.User;
import com.example.addressapi.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User(null, request.getUsername(), passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return new AuthResponse(user.getUsername(), "Registered successfully");
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return new AuthResponse(user.getUsername(), "Login successful");
    }
}