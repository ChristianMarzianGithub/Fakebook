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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private FollowService followService;

    private User follower;
    private User following;

    @BeforeEach
    void setUp() {
        follower = User.builder().id(1L).username("alice").build();
        following = User.builder().id(2L).username("bob").build();
    }

    @Test
    void followUserRejectsSelfFollow() {
        assertThatThrownBy(() -> followService.followUser(follower, follower.getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("cannot follow themselves");

        verifyNoInteractions(userRepository, followRepository);
    }

    @Test
    void followUserRejectsDuplicateFollow() {
        when(userRepository.findById(following.getId())).thenReturn(Optional.of(following));
        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(true);

        assertThatThrownBy(() -> followService.followUser(follower, following.getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Already following");

        verify(userRepository).findById(following.getId());
        verify(followRepository).existsByFollowerAndFollowing(follower, following);
        verify(followRepository, never()).save(any(Follow.class));
    }

    @Test
    void followUserPersistsWhenValid() {
        when(userRepository.findById(following.getId())).thenReturn(Optional.of(following));
        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(false);

        followService.followUser(follower, following.getId());

        ArgumentCaptor<Follow> followCaptor = ArgumentCaptor.forClass(Follow.class);
        verify(followRepository).save(followCaptor.capture());
        Follow savedFollow = followCaptor.getValue();

        assertThat(savedFollow.getFollower()).isEqualTo(follower);
        assertThat(savedFollow.getFollowing()).isEqualTo(following);
        assertThat(savedFollow.getId()).isEqualTo(new FollowId(follower.getId(), following.getId()));
    }

    @Test
    void followUserThrowsWhenTargetMissing() {
        when(userRepository.findById(following.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> followService.followUser(follower, following.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(following.getId());
        verifyNoMoreInteractions(followRepository);
    }

    @Test
    void getFollowersResolvesAndMapsResponses() {
        User followerTwo = User.builder().id(3L).username("carol").build();
        Follow followOne = Follow.builder()
                .id(new FollowId(follower.getId(), following.getId()))
                .follower(follower)
                .following(following)
                .build();
        Follow followTwo = Follow.builder()
                .id(new FollowId(followerTwo.getId(), following.getId()))
                .follower(followerTwo)
                .following(following)
                .build();
        FollowResponse responseOne = new FollowResponse();
        FollowResponse responseTwo = new FollowResponse();
        when(userRepository.findById(following.getId())).thenReturn(Optional.of(following));
        when(followRepository.findAllByFollowing(following)).thenReturn(List.of(followOne, followTwo));
        when(userMapper.toFollowResponse(follower)).thenReturn(responseOne);
        when(userMapper.toFollowResponse(followerTwo)).thenReturn(responseTwo);

        List<FollowResponse> responses = followService.getFollowers(following.getId());

        assertThat(responses).containsExactly(responseOne, responseTwo);
        verify(userRepository).findById(following.getId());
        verify(followRepository).findAllByFollowing(following);
        verify(userMapper).toFollowResponse(follower);
        verify(userMapper).toFollowResponse(followerTwo);
    }

    @Test
    void getFollowersThrowsWhenUserMissing() {
        when(userRepository.findById(following.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> followService.getFollowers(following.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(following.getId());
        verifyNoMoreInteractions(followRepository, userMapper);
    }

    @Test
    void getFollowingResolvesAndMapsResponses() {
        Follow followOne = Follow.builder()
                .id(new FollowId(follower.getId(), following.getId()))
                .follower(follower)
                .following(following)
                .build();
        FollowResponse response = new FollowResponse();
        when(userRepository.findById(follower.getId())).thenReturn(Optional.of(follower));
        when(followRepository.findAllByFollower(follower)).thenReturn(List.of(followOne));
        when(userMapper.toFollowResponse(following)).thenReturn(response);

        List<FollowResponse> responses = followService.getFollowing(follower.getId());

        assertThat(responses).containsExactly(response);
        verify(userRepository).findById(follower.getId());
        verify(followRepository).findAllByFollower(follower);
        verify(userMapper).toFollowResponse(following);
    }
}
