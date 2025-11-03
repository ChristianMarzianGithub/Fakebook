package com.example.fakebook.dto.user;

import lombok.Data;

@Data
public class FollowResponse {
    private Long id;
    private String username;
    private String profileImageUrl;
}
