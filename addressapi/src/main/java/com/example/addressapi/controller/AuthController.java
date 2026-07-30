package com.example.addressapi.controller;

import com.example.addressapi.dto.LoginRequest;
import com.example.addressapi.dto.RegisterRequest;
import com.example.addressapi.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(409).body(new ErrorBody(ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(new ErrorBody(ex.getMessage()));
        }
    }

    record ErrorBody(String message) {}
}