package com.example.fakebook.service;

import com.example.fakebook.dto.post.PostRequest;
import com.example.fakebook.dto.post.PostResponse;
import com.example.fakebook.entity.*;
import com.example.fakebook.exception.BadRequestException;
import com.example.fakebook.exception.ResourceNotFoundException;
import com.example.fakebook.mapper.PostMapper;
import com.example.fakebook.repository.FollowRepository;
import com.example.fakebook.repository.PostLikeRepository;
import com.example.fakebook.repository.PostRepository;
import com.example.fakebook.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private FollowRepository followRepository;
    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("john").build();
        post = Post.builder().id(10L).user(user).content("Hello").build();
    }

    @Test
    void createPostSavesEntity() {
        PostRequest request = new PostRequest();
        request.setContent("Hello");
        when(postMapper.toEntity(request)).thenReturn(Post.builder().content("Hello").build());
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(new PostResponse());
        when(postLikeRepository.countByPost(post)).thenReturn(0L);

        PostResponse response = postService.createPost(user, request);

        assertThat(response).isNotNull();
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void likePostThrowsWhenAlreadyLiked() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postLikeRepository.existsByPostAndUser(post, user)).thenReturn(true);

        assertThatThrownBy(() -> postService.likePost(user, 10L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already liked");
    }

    @Test
    void getNewsfeedIncludesOwnPostsWhenNoFollows() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(followRepository.findAllByFollower(user)).thenReturn(List.of());
        when(postRepository.findAllByUserIn(any(), eq(pageable))).thenReturn(new PageImpl<>(List.of(post)));
        when(postMapper.toResponse(post)).thenReturn(new PostResponse());
        when(postLikeRepository.countByPost(post)).thenReturn(0L);

        Page<PostResponse> page = postService.getNewsfeed(user, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    void deletePostThrowsForDifferentOwner() {
        User other = User.builder().id(2L).build();
        post.setUser(other);
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.deletePost(user, 10L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void getPostThrowsWhenMissing() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPost(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
