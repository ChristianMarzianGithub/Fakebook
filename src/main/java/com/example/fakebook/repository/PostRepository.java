package com.example.fakebook.repository;

import com.example.fakebook.entity.Post;
import com.example.fakebook.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByUser(User user, Pageable pageable);
    Page<Post> findAllByUserIn(Collection<User> users, Pageable pageable);
}
