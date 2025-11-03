package com.example.fakebook.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostRequest {

    @NotBlank
    @Size(max = 5000)
    private String content;

    @Size(max = 255)
    private String imageUrl;
}
