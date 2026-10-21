package com.example.studentapi.controller;

import com.example.studentapi.dto.AuthResponse;
import com.example.studentapi.dto.LoginRequest;
import com.example.studentapi.dto.RefreshRequest;
import com.example.studentapi.dto.RegisterRequest;
import com.example.studentapi.model.RefreshToken;
import com.example.studentapi.model.User;
import com.example.studentapi.response.ApiResponse;
import com.example.studentapi.security.JwtService;
import com.example.studentapi.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(
            AuthService authService,
            JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest request) {

        String message = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, message, null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        User user = authService.verifyCredentials(request);
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = authService.createRefreshToken(user);

        AuthResponse authResponse = new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtService.getExpirationMillis(),
                user.getUsername(),
                user.getRole().name()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Login successful",
                        authResponse
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshRequest request) {

        RefreshToken refreshToken = authService.verifyRefreshToken(request.getRefreshToken());
        User user = refreshToken.getUser();
        String accessToken = jwtService.generateAccessToken(user);

        AuthResponse authResponse = new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtService.getExpirationMillis(),
                user.getUsername(),
                user.getRole().name()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Token refreshed successfully",
                        authResponse
                )
        );
    }
}
