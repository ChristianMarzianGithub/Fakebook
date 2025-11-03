package com.example.fakebook.service;

import com.example.fakebook.dto.comment.CommentRequest;
import com.example.fakebook.dto.comment.CommentResponse;
import com.example.fakebook.entity.Comment;
import com.example.fakebook.entity.Post;
import com.example.fakebook.entity.User;
import com.example.fakebook.exception.BadRequestException;
import com.example.fakebook.exception.ResourceNotFoundException;
import com.example.fakebook.mapper.CommentMapper;
import com.example.fakebook.repository.CommentRepository;
import com.example.fakebook.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("john").build();
        post = Post.builder().id(2L).build();
        comment = Comment.builder().id(3L).user(user).post(post).content("Nice!").build();
    }

    @Test
    void addCommentPersistsEntity() {
        CommentRequest request = new CommentRequest();
        request.setContent("Great post");
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));
        when(commentMapper.toEntity(request)).thenReturn(new Comment());
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponse(comment)).thenReturn(new CommentResponse());

        CommentResponse response = commentService.addComment(user, 2L, request);

        assertThat(response).isNotNull();
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void updateCommentThrowsWhenNotOwner() {
        CommentRequest request = new CommentRequest();
        request.setContent("Updated");
        User other = User.builder().id(5L).build();
        comment.setUser(other);
        when(commentRepository.findById(3L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.updateComment(user, 3L, request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void deleteCommentThrowsWhenMissing() {
        when(commentRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(user, 7L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
