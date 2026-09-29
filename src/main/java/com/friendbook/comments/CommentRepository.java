package com.friendbook.comments;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	boolean existsByUser_UserIdAndPost_PostId(Long userId, Long postId);

	Optional<List<Comment>> findByPost_PostId(Long postId);

	@Modifying
	@Query("DELETE FROM Comment c WHERE c.post.postId = :postId")
	void deleteByPost_PostId(Long postId);

}
