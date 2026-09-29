package com.friendbook.likes;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.friendbook.posts.Post;
import com.friendbook.user.User;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	void deleteByUserAndPost(User user, Post post);

	Long countByPost_PostId(Long postId);

	boolean existsByUser_UserIdAndPost_PostId(Long userId, Long postId);

	@Modifying
	@Query("DELETE FROM PostLike pl WHERE pl.post.postId = :postId")
	void deleteByPost_PostId(Long postId);

	@Query("""
			    SELECT pl.user.userName
			    FROM PostLike pl
			    WHERE pl.post.postId = :postId
			""")
	List<String> findUserNamesByPostId(Long postId);

}
