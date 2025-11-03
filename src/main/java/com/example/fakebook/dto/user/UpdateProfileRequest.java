package com.example.fakebook.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(max = 255)
    private String bio;

    @Size(max = 255)
    private String profileImageUrl;
}
