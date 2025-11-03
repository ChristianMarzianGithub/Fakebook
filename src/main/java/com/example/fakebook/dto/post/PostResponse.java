package com.example.fakebook.dto.post;

import com.example.fakebook.dto.user.UserResponse;
import lombok.Data;

import java.time.Instant;

@Data
public class PostResponse {
    private Long id;
    private String content;
    private String imageUrl;
    private Instant createdAt;
    private long likeCount;
    private UserResponse author;
}
