package com.example.fakebook.service;

import com.example.fakebook.dto.user.FollowResponse;
import com.example.fakebook.dto.user.UpdateProfileRequest;
import com.example.fakebook.dto.user.UserResponse;
import com.example.fakebook.entity.Follow;
import com.example.fakebook.entity.User;
import com.example.fakebook.exception.ResourceNotFoundException;
import com.example.fakebook.mapper.UserMapper;
import com.example.fakebook.repository.FollowRepository;
import com.example.fakebook.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private FollowRepository followRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserProfileReturnsMappedResponse() {
        User user = User.builder().id(5L).username("alice").build();
        UserResponse response = new UserResponse();
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(response);

        UserResponse result = userService.getUserProfile(5L);

        assertThat(result).isSameAs(response);
        verify(userRepository).findById(5L);
        verify(userMapper).toUserResponse(user);
    }

    @Test
    void getUserProfileThrowsWhenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserProfile(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userMapper, never()).toUserResponse(any());
    }

    @Test
    void updateProfileDelegatesToMapperAndSaves() {
        User user = User.builder().id(3L).username("bob").build();
        UpdateProfileRequest request = new UpdateProfileRequest();
        User savedUser = User.builder().id(3L).username("bob-updated").build();
        UserResponse response = new UserResponse();
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toUserResponse(savedUser)).thenReturn(response);

        UserResponse result = userService.updateProfile(user, request);

        assertThat(result).isSameAs(response);
        verify(userMapper).updateUserFromRequest(request, user);
        verify(userRepository).save(user);
        verify(userMapper).toUserResponse(savedUser);
    }

    @Test
    void getFollowersTransformsToDto() {
        User currentUser = User.builder().id(1L).username("target").build();
        User followerUser = User.builder().id(2L).username("follower").build();
        Follow follow = Follow.builder().follower(followerUser).following(currentUser).build();
        FollowResponse dto = new FollowResponse();
        when(followRepository.findAllByFollowing(currentUser)).thenReturn(List.of(follow));
        when(userMapper.toFollowResponse(followerUser)).thenReturn(dto);

        List<FollowResponse> result = userService.getFollowers(currentUser);

        assertThat(result).containsExactly(dto);
        verify(followRepository).findAllByFollowing(currentUser);
        verify(userMapper).toFollowResponse(followerUser);
    }

    @Test
    void getFollowingTransformsToDto() {
        User currentUser = User.builder().id(1L).username("target").build();
        User followingUser = User.builder().id(3L).username("following").build();
        Follow follow = Follow.builder().follower(currentUser).following(followingUser).build();
        FollowResponse dto = new FollowResponse();
        when(followRepository.findAllByFollower(currentUser)).thenReturn(List.of(follow));
        when(userMapper.toFollowResponse(followingUser)).thenReturn(dto);

        List<FollowResponse> result = userService.getFollowing(currentUser);

        assertThat(result).containsExactly(dto);
        verify(followRepository).findAllByFollower(currentUser);
        verify(userMapper).toFollowResponse(followingUser);
    }
}
