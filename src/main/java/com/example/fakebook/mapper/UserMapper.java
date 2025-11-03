package com.example.fakebook.mapper;

import com.example.fakebook.dto.user.FollowResponse;
import com.example.fakebook.dto.user.UpdateProfileRequest;
import com.example.fakebook.dto.user.UserResponse;
import com.example.fakebook.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toUserResponse(User user);

    FollowResponse toFollowResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromRequest(UpdateProfileRequest request, @MappingTarget User user);
}
