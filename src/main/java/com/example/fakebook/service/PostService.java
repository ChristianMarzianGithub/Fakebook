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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final FollowRepository followRepository;
    private final PostMapper postMapper;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       PostLikeRepository postLikeRepository,
                       FollowRepository followRepository,
                       PostMapper postMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postLikeRepository = postLikeRepository;
        this.followRepository = followRepository;
        this.postMapper = postMapper;
    }

    public PostResponse createPost(User user, PostRequest request) {
        Post post = postMapper.toEntity(request);
        post.setUser(user);
        Post saved = postRepository.save(post);
        return enrichPost(saved);
    }

    public Page<PostResponse> getUserPosts(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return postRepository.findAllByUser(user, pageable)
                .map(this::enrichPost);
    }

    public Page<PostResponse> getNewsfeed(User user, Pageable pageable) {
        List<User> following = followRepository.findAllByFollower(user).stream()
                .map(Follow::getFollowing)
                .toList();
        if (following.isEmpty()) {
            following = List.of(user);
        } else if (!following.contains(user)) {
            following = new java.util.ArrayList<>(following);
            following.add(user);
        }
        return postRepository.findAllByUserIn(following, pageable)
                .map(this::enrichPost);
    }

    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        return enrichPost(post);
    }

    public void deletePost(User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        if (!post.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Cannot delete another user's post");
        }
        postRepository.delete(post);
    }

    public void likePost(User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        if (postLikeRepository.existsByPostAndUser(post, user)) {
            throw new BadRequestException("Post already liked");
        }
        PostLike like = PostLike.builder()
                .id(new PostLikeId(user.getId(), post.getId()))
                .post(post)
                .user(user)
                .build();
        postLikeRepository.save(like);
    }

    public void unlikePost(User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        postLikeRepository.deleteByPostAndUser(post, user);
    }

    private PostResponse enrichPost(Post post) {
        PostResponse response = postMapper.toResponse(post);
        response.setLikeCount(postLikeRepository.countByPost(post));
        return response;
    }
}
