package com.friendbook.likes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.friendbook.exception.PostNotFoundException;
import com.friendbook.notifications.NotificationService;
import com.friendbook.notifications.NotificationType;
import com.friendbook.posts.Post;
import com.friendbook.posts.PostRepository;
import com.friendbook.user.User;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {

	@Mock
	private PostLikeRepository postLikeRepo;

	@Mock
	private PostRepository postRepository;

	@Mock
	private NotificationService notificationService;

	@InjectMocks
	private PostLikeService postLikeService;

	@Test
	void addPostLike_shouldCreateLikeAndNotification_whenPostIsNotAlreadyLiked() {

		Long postId = 10L;

		User user = new User();
		user.setUserId(1L);

		User postOwner = new User();
		postOwner.setUserId(2L);

		Post post = new Post();
		post.setPostId(postId);
		post.setUser(postOwner);

		when(postRepository.findById(postId)).thenReturn(Optional.of(post));

		when(postLikeRepo.existsByUser_UserIdAndPost_PostId(1L, postId)).thenReturn(false);

		postLikeService.addPostLike(postId, user);

		verify(postLikeRepo).save(any(PostLike.class));

		verify(notificationService).createNotification(postOwner, user, NotificationType.LIKE, post);
	}

	@Test
	void addPostLike_shouldDoNothing_whenUserAlreadyLikedPost() {

		Long postId = 10L;

		User user = new User();
		user.setUserId(1L);

		Post post = new Post();
		post.setPostId(postId);

		when(postRepository.findById(postId)).thenReturn(Optional.of(post));

		when(postLikeRepo.existsByUser_UserIdAndPost_PostId(1L, postId)).thenReturn(true);

		postLikeService.addPostLike(postId, user);

		verify(postLikeRepo, never()).save(any(PostLike.class));

		verify(notificationService, never()).createNotification(any(), any(), any(), any());
	}

	@Test
	void addPostLike_shouldThrowException_whenPostDoesNotExist() {

		Long postId = 10L;

		User user = new User();
		user.setUserId(1L);

		when(postRepository.findById(postId)).thenReturn(Optional.empty());

		assertThrows(PostNotFoundException.class, () -> postLikeService.addPostLike(postId, user));

		verify(postLikeRepo, never()).save(any(PostLike.class));

		verify(notificationService, never()).createNotification(any(), any(), any(), any());
	}

	@Test
	void unlikePost_shouldDeleteLike_whenPostExists() {

		Long postId = 10L;

		User user = new User();
		user.setUserId(1L);

		Post post = new Post();
		post.setPostId(postId);

		when(postRepository.findById(postId)).thenReturn(Optional.of(post));

		postLikeService.unlikePost(postId, user);

		verify(postLikeRepo).deleteByUserAndPost(user, post);
	}

	@Test
	void unlikePost_shouldThrowException_whenPostDoesNotExist() {

		Long postId = 10L;

		User user = new User();
		user.setUserId(1L);

		when(postRepository.findById(postId)).thenReturn(Optional.empty());

		assertThrows(PostNotFoundException.class, () -> postLikeService.unlikePost(postId, user));

		verify(postLikeRepo, never()).deleteByUserAndPost(any(), any());
	}

	@Test
	void getPostLikeCountByPostId_shouldReturnLikeCount() {

		Long postId = 10L;

		when(postLikeRepo.countByPost_PostId(postId)).thenReturn(15L);

		Long result = postLikeService.getPostLikeCountByPostId(postId);

		assertEquals(15L, result);

		verify(postLikeRepo).countByPost_PostId(postId);
	}

	@Test
	void getAllPostLikedBy_shouldReturnLikedUsersAndCount() {

		Long postId = 10L;

		List<String> userNames = List.of("rishi", "john", "alex");

		when(postLikeRepo.findUserNamesByPostId(postId)).thenReturn(userNames);

		ViewPostLikesDTO result = postLikeService.getAllPostLikedBy(postId);

		assertEquals(postId, result.getPostId());
		assertEquals(userNames, result.getUserName());
		assertEquals(3L, result.getCount());

		verify(postLikeRepo).findUserNamesByPostId(postId);
	}

	@Test
	void getAllPostLikedBy_shouldReturnZeroCount_whenNobodyLikedPost() {

		Long postId = 10L;

		when(postLikeRepo.findUserNamesByPostId(postId)).thenReturn(List.of());

		ViewPostLikesDTO result = postLikeService.getAllPostLikedBy(postId);

		assertEquals(postId, result.getPostId());
		assertTrue(result.getUserName().isEmpty());
		assertEquals(0L, result.getCount());
	}
}