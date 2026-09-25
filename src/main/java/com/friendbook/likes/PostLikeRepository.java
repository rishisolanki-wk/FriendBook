package com.friendbook.likes;

import org.springframework.data.jpa.repository.JpaRepository;

import com.friendbook.posts.Post;
import com.friendbook.user.User;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	void deleteByUserAndPost(User user, Post post);

	Long countByPost_PostId(Long postId);

	boolean existsByUser_UserIdAndPost_PostId(Long userId, Long postId);

	void deleteByPost_PostId(Long id);

}
