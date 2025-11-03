package com.example.fakebook.controller;

import com.example.fakebook.dto.comment.CommentRequest;
import com.example.fakebook.dto.comment.CommentResponse;
import com.example.fakebook.service.AuthService;
import com.example.fakebook.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final AuthService authService;
    private final CommentService commentService;

    public CommentController(AuthService authService, CommentService commentService) {
        this.authService = authService;
        this.commentService = commentService;
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long postId,
                                                       @Valid @RequestBody CommentRequest request) {
        var user = authService.getCurrentUser();
        return ResponseEntity.ok(commentService.addComment(user, postId, request));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long commentId,
                                                          @Valid @RequestBody CommentRequest request) {
        var user = authService.getCurrentUser();
        return ResponseEntity.ok(commentService.updateComment(user, commentId, request));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        var user = authService.getCurrentUser();
        commentService.deleteComment(user, commentId);
        return ResponseEntity.noContent().build();
    }
}
