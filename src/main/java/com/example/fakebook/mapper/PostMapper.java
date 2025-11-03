package com.example.fakebook.mapper;

import com.example.fakebook.dto.post.PostRequest;
import com.example.fakebook.dto.post.PostResponse;
import com.example.fakebook.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface PostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Post toEntity(PostRequest request);

    @Mapping(target = "author", source = "user")
    @Mapping(target = "likeCount", ignore = true)
    PostResponse toResponse(Post post);
}
