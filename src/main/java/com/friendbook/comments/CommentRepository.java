package com.friendbook.comments;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	boolean existsByUser_UserIdAndPost_PostId(Long userId, Long postId);

	Optional<List<Comment>> findByPost_PostId(Long postId);

}
