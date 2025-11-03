package com.example.fakebook.controller;

import com.example.fakebook.dto.post.PostRequest;
import com.example.fakebook.dto.post.PostResponse;
import com.example.fakebook.service.AuthService;
import com.example.fakebook.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final AuthService authService;
    private final PostService postService;

    public PostController(AuthService authService, PostService postService) {
        this.authService = authService;
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest request) {
        var user = authService.getCurrentUser();
        return ResponseEntity.ok(postService.createPost(user, request));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        var user = authService.getCurrentUser();
        postService.deletePost(user, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponse>> getUserPosts(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(postService.getUserPosts(userId, pageable));
    }

    @GetMapping("/feed")
    public ResponseEntity<Page<PostResponse>> getNewsfeed(Pageable pageable) {
        var user = authService.getCurrentUser();
        return ResponseEntity.ok(postService.getNewsfeed(user, pageable));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long postId) {
        var user = authService.getCurrentUser();
        postService.likePost(user, postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId) {
        var user = authService.getCurrentUser();
        postService.unlikePost(user, postId);
        return ResponseEntity.noContent().build();
    }
}
