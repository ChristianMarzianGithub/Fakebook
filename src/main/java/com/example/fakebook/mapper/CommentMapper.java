package com.example.fakebook.mapper;

import com.example.fakebook.dto.comment.CommentRequest;
import com.example.fakebook.dto.comment.CommentResponse;
import com.example.fakebook.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Comment toEntity(CommentRequest request);

    @Mapping(target = "author", source = "user")
    CommentResponse toResponse(Comment comment);
}
