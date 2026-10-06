package com.friendbook.comments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.friendbook.exception.ResourceNotFoundException;
import com.friendbook.notifications.NotificationService;
import com.friendbook.notifications.NotificationType;
import com.friendbook.posts.Post;
import com.friendbook.posts.PostRepository;
import com.friendbook.user.User;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private PostRepository postRepository;

	@Mock
	private NotificationService notificationService;

	@InjectMocks
	private CommentService commentService;

	@Test
	void addCommentByPostId_shouldSaveCommentAndCreateNotification_whenValid() {

		Long postId = 10L;

		User user = new User();
		user.setUserId(1L);

		User postOwner = new User();
		postOwner.setUserId(2L);

		Post post = new Post();
		post.setPostId(postId);
		post.setUser(postOwner);

		CommentRequestDTO request = new CommentRequestDTO();
		request.setComment("Nice post!");

		when(commentRepository.existsByUser_UserIdAndPost_PostId(1L, postId)).thenReturn(false);

		when(postRepository.findById(postId)).thenReturn(Optional.of(post));

		commentService.addCommentByPostId(postId, request, user);

		verify(commentRepository).save(any(Comment.class));

		verify(notificationService).createNotification(postOwner, user, NotificationType.COMMENT, post);
	}

	@Test
	void addCommentByPostId_shouldDoNothing_whenUserAlreadyCommented() {

		Long postId = 10L;

		User user = new User();
		user.setUserId(1L);

		CommentRequestDTO request = new CommentRequestDTO();
		request.setComment("Another comment");

		when(commentRepository.existsByUser_UserIdAndPost_PostId(1L, postId)).thenReturn(true);

		commentService.addCommentByPostId(postId, request, user);

		verify(commentRepository, never()).save(any(Comment.class));

		verify(postRepository, never()).findById(anyLong());

		verify(notificationService, never()).createNotification(any(), any(), any(), any());
	}

	@Test
	void addCommentByPostId_shouldThrowException_whenPostDoesNotExist() {

		Long postId = 10L;

		User user = new User();
		user.setUserId(1L);

		CommentRequestDTO request = new CommentRequestDTO();
		request.setComment("Nice post!");

		when(commentRepository.existsByUser_UserIdAndPost_PostId(1L, postId)).thenReturn(false);

		when(postRepository.findById(postId)).thenReturn(Optional.empty());

		assertThrows(PostNotFoundException.class, () -> commentService.addCommentByPostId(postId, request, user));

		verify(commentRepository, never()).save(any(Comment.class));

		verify(notificationService, never()).createNotification(any(), any(), any(), any());
	}

	@Test
	void getCommentsByPostId_shouldReturnComments_whenCommentsExist() {

		Long postId = 10L;

		User user = new User();
		user.setUserId(1L);

		Post post = new Post();
		post.setPostId(postId);

		Comment comment = new Comment();
		comment.setCommentId(100L);
		comment.setCommentMsg("Nice!");
		comment.setPost(post);
		comment.setUser(user);

		when(commentRepository.findByPost_PostId(postId)).thenReturn(Optional.of(List.of(comment)));

		List<CommentResponseDTO> result = commentService.getCommentsByPostId(postId);

		assertEquals(1, result.size());

		CommentResponseDTO dto = result.get(0);

		assertEquals(100L, dto.getCommentId());
		assertEquals("Nice!", dto.getCommentMsg());
		assertEquals(post, dto.getPost());
		assertEquals(user, dto.getUser());

		verify(commentRepository).findByPost_PostId(postId);
	}

	@Test
	void getCommentsByPostId_shouldThrowException_whenCommentsNotFound() {

		Long postId = 10L;

		when(commentRepository.findByPost_PostId(postId)).thenReturn(Optional.empty());

		assertThrows(PostNotFoundException.class, () -> commentService.getCommentsByPostId(postId));
	}

	@Test
	void removeCommentById_shouldDeleteComment_whenUserIsOwner() {

		Long commentId = 100L;

		User owner = new User();
		owner.setUserId(1L);

		Comment comment = new Comment();
		comment.setCommentId(commentId);
		comment.setUser(owner);

		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

		User currentUser = new User();
		currentUser.setUserId(1L);

		commentService.removeCommentById(commentId, currentUser);

		verify(commentRepository).delete(comment);
	}

	@Test
	void removeCommentById_shouldDoNothing_whenUserIsNotOwner() {

		Long commentId = 100L;

		User owner = new User();
		owner.setUserId(1L);

		Comment comment = new Comment();
		comment.setCommentId(commentId);
		comment.setUser(owner);

		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

		User anotherUser = new User();
		anotherUser.setUserId(2L);

		commentService.removeCommentById(commentId, anotherUser);

		verify(commentRepository, never()).delete(any(Comment.class));
	}

	@Test
	void removeCommentById_shouldThrowException_whenCommentDoesNotExist() {

		Long commentId = 100L;

		when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

		User user = new User();

		assertThrows(ResourceNotFoundException.class, () -> commentService.removeCommentById(commentId, user));

		verify(commentRepository, never()).delete(any(Comment.class));
	}
}