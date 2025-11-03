package com.example.fakebook.repository;

import com.example.fakebook.entity.Comment;
import com.example.fakebook.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
}
