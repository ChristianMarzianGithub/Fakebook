package com.example.fakebook.controller;

import com.example.fakebook.service.AuthService;
import com.example.fakebook.service.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    private final AuthService authService;
    private final FollowService followService;

    public FollowController(AuthService authService, FollowService followService) {
        this.authService = authService;
        this.followService = followService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Void> follow(@PathVariable Long userId) {
        var user = authService.getCurrentUser();
        followService.followUser(user, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unfollow(@PathVariable Long userId) {
        var user = authService.getCurrentUser();
        followService.unfollowUser(user, userId);
        return ResponseEntity.noContent().build();
    }
}
