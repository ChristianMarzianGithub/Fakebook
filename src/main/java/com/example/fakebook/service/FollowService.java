package com.example.fakebook.service;

import com.example.fakebook.dto.user.FollowResponse;
import com.example.fakebook.entity.Follow;
import com.example.fakebook.entity.FollowId;
import com.example.fakebook.entity.User;
import com.example.fakebook.exception.BadRequestException;
import com.example.fakebook.exception.ResourceNotFoundException;
import com.example.fakebook.mapper.UserMapper;
import com.example.fakebook.repository.FollowRepository;
import com.example.fakebook.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public FollowService(FollowRepository followRepository, UserRepository userRepository, UserMapper userMapper) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public void followUser(User follower, Long userId) {
        if (follower.getId().equals(userId)) {
            throw new BadRequestException("Users cannot follow themselves");
        }
        User following = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new BadRequestException("Already following user");
        }
        Follow follow = Follow.builder()
                .id(new FollowId(follower.getId(), following.getId()))
                .follower(follower)
                .following(following)
                .build();
        followRepository.save(follow);
    }

    public void unfollowUser(User follower, Long userId) {
        User following = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        followRepository.deleteByFollowerAndFollowing(follower, following);
    }

    public List<FollowResponse> getFollowers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return followRepository.findAllByFollowing(user).stream()
                .map(follow -> userMapper.toFollowResponse(follow.getFollower()))
                .toList();
    }

    public List<FollowResponse> getFollowing(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return followRepository.findAllByFollower(user).stream()
                .map(follow -> userMapper.toFollowResponse(follow.getFollowing()))
                .toList();
    }
}
