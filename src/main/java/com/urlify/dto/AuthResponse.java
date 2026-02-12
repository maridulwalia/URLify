package com.urlify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String email;
    private String message;
    private UserResponse user;

    public AuthResponse(String token, String email, String message) {
        this.token = token;
        this.email = email;
        this.message = message;
    }
}
