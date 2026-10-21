package com.example.studentapi.service;

import com.example.studentapi.dto.LoginRequest;
import com.example.studentapi.dto.RegisterRequest;
import com.example.studentapi.model.RefreshToken;
import com.example.studentapi.model.Role;
import com.example.studentapi.model.User;
import com.example.studentapi.repository.RefreshTokenRepository;
import com.example.studentapi.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final long refreshTokenExpiration;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            @Value("${security.jwt.refresh-token-expiration:604800000}") long refreshTokenExpiration) {

        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username is already taken"
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // Never store the original password.
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // Public registration must not grant ADMIN access.
        user.setRole(Role.USER);

        userRepository.save(user);

        return "User registered successfully";
    }

    public User verifyCredentials(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid username or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password"
            );
        }

        return user;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseGet(RefreshToken::new);

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiration));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid refresh token"
                ));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token has expired or is revoked"
            );
        }

        return refreshToken;
    }
}
