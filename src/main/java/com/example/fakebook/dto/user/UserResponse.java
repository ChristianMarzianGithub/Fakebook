package com.example.fakebook.dto.user;

import lombok.Data;

import java.time.Instant;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String profileImageUrl;
    private Instant createdAt;
}
