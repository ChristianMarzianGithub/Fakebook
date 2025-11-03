package com.example.fakebook.repository;

import com.example.fakebook.entity.Post;
import com.example.fakebook.entity.PostLike;
import com.example.fakebook.entity.PostLikeId;
import com.example.fakebook.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    boolean existsByPostAndUser(Post post, User user);
    long countByPost(Post post);
    void deleteByPostAndUser(Post post, User user);
}
