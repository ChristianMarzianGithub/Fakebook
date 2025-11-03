package com.example.fakebook.service;

import com.example.fakebook.dto.user.FollowResponse;
import com.example.fakebook.dto.user.UpdateProfileRequest;
import com.example.fakebook.dto.user.UserResponse;
import com.example.fakebook.entity.User;
import com.example.fakebook.exception.ResourceNotFoundException;
import com.example.fakebook.mapper.UserMapper;
import com.example.fakebook.repository.FollowRepository;
import com.example.fakebook.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, FollowRepository followRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.userMapper = userMapper;
    }

    public UserResponse getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateProfile(User user, UpdateProfileRequest request) {
        userMapper.updateUserFromRequest(request, user);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public List<FollowResponse> getFollowers(User user) {
        return followRepository.findAllByFollowing(user).stream()
                .map(follow -> userMapper.toFollowResponse(follow.getFollower()))
                .toList();
    }

    public List<FollowResponse> getFollowing(User user) {
        return followRepository.findAllByFollower(user).stream()
                .map(follow -> userMapper.toFollowResponse(follow.getFollowing()))
                .toList();
    }
}
