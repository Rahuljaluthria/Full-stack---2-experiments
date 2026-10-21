
package com.example.studentapi.dto;

public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private String username;
    private String role;

    public AuthResponse() {
    }

    public AuthResponse(
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresIn,
            String username,
            String role) {

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.username = username;
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
