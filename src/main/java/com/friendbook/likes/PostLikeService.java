package com.friendbook.likes;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.friendbook.notifications.NotificationService;
import com.friendbook.notifications.NotificationType;
import com.friendbook.posts.Post;
import com.friendbook.posts.PostRepository;
import com.friendbook.user.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostLikeService {

	private final PostLikeRepository postLikeRepo;
	private final PostRepository postRepository;
	private final NotificationService notificationService;

	public void addPostLike(Long postId, User user) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post Not Found!"));
		if (postLikeRepo.existsByUser_UserIdAndPost_PostId(user.getUserId(), postId)) {
			return;
		}
		PostLike postLike = new PostLike();
		postLike.setCreatedAt(LocalDateTime.now());
		postLike.setPost(post);
		postLike.setUser(user);
		postLikeRepo.save(postLike);
		notificationService.createNotification(post.getUser(), user, NotificationType.LIKE, post);
	}

	@Transactional
	public void unlikePost(Long postId, User user) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post Not Found!"));
		postLikeRepo.deleteByUserAndPost(user, post);
	}

	public Long getPostLikeCountByPostId(Long postId) {
		return postLikeRepo.countByPost_PostId(postId);
	}

	public ViewPostLikesDTO getAllPostLikedBy(Long postId) {
		List<String> userNames = postLikeRepo.findUserNamesByPostId(postId);
		ViewPostLikesDTO viewPostLikesDTO = new ViewPostLikesDTO();
		viewPostLikesDTO.setUserName(userNames);
		viewPostLikesDTO.setPostId(postId);
		viewPostLikesDTO.setCount((long) userNames.size());
		return viewPostLikesDTO;
	}

}
