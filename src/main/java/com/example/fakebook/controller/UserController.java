package com.example.fakebook.controller;

import com.example.fakebook.dto.user.FollowResponse;
import com.example.fakebook.dto.user.UpdateProfileRequest;
import com.example.fakebook.dto.user.UserResponse;
import com.example.fakebook.service.AuthService;
import com.example.fakebook.service.FollowService;
import com.example.fakebook.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthService authService;
    private final UserService userService;
    private final FollowService followService;

    public UserController(AuthService authService, UserService userService, FollowService followService) {
        this.authService = authService;
        this.userService = userService;
        this.followService = followService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        var user = authService.getCurrentUser();
        return ResponseEntity.ok(userService.getUserProfile(user.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        var user = authService.getCurrentUser();
        return ResponseEntity.ok(userService.updateProfile(user, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<List<FollowResponse>> getFollowers(@PathVariable Long id) {
        return ResponseEntity.ok(followService.getFollowers(id));
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<List<FollowResponse>> getFollowing(@PathVariable Long id) {
        return ResponseEntity.ok(followService.getFollowing(id));
    }
}
