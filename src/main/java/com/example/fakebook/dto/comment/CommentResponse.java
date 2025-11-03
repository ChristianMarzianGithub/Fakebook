package com.example.fakebook.dto.comment;

import com.example.fakebook.dto.user.UserResponse;
import lombok.Data;

import java.time.Instant;

@Data
public class CommentResponse {
    private Long id;
    private String content;
    private Instant createdAt;
    private UserResponse author;
}
