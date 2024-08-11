package com.doston.Dictionary.repo;

import com.doston.Dictionary.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// CommentRepository.java
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {}
