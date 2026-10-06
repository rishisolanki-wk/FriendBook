package com.friendbook.posts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.friendbook.comments.CommentRepository;
import com.friendbook.exception.PostNotFoundException;
import com.friendbook.likes.PostLikeRepository;
import com.friendbook.user.User;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private PostRepository postRepository;

	@Mock
	private CloudinaryService cloudinaryService;

	@Mock
	private PostLikeRepository postLikeRepository;

	@InjectMocks
	private PostService postService;

	@Test
	void createPost_shouldSavePostAndReturnSuccess_whenImageUploadSucceeds() {

		CreatePostRequestDTO request = new CreatePostRequestDTO();
		MultipartFile image = mock(MultipartFile.class);
		request.setImage(image);
		request.setCaption("My first post");

		User user = new User();
		user.setUserId(1L);

		CloudinaryUploadResult uploadResult = mock(CloudinaryUploadResult.class);

		when(uploadResult.getImageUrl()).thenReturn("image-url");
		when(uploadResult.getImagePublicId()).thenReturn("image-public-id");

		when(cloudinaryService.uploadImage(image)).thenReturn(uploadResult);

		String result = postService.createPost(request, user);

		assertEquals("Image Uploaded Successfully", result);

		verify(cloudinaryService).uploadImage(image);
		verify(postRepository).save(any(Post.class));
	}

	@Test
	void createPost_shouldNotSavePostAndReturnFailure_whenImageUploadFails() {

		CreatePostRequestDTO request = new CreatePostRequestDTO();
		MultipartFile image = mock(MultipartFile.class);
		request.setImage(image);

		User user = new User();

		when(cloudinaryService.uploadImage(image)).thenThrow(new RuntimeException("Cloudinary failed"));

		String result = postService.createPost(request, user);

		assertEquals("Image Not Uploaded!", result);

		verify(cloudinaryService).uploadImage(image);
		verify(postRepository, never()).save(any(Post.class));
	}

	@Test
	void getPostByUserId_shouldReturnPostDTOs_whenPostsExist() {

		Long userId = 1L;

		Post post = new Post();
		post.setPostId(10L);
		post.setCaption("Hello");
		post.setImageUrl("image-url");

		when(postRepository.findByUser_UserId(userId)).thenReturn(Optional.of(List.of(post)));

		when(postLikeRepository.countByPost_PostId(10L)).thenReturn(5L);

		when(postLikeRepository.existsByUser_UserIdAndPost_PostId(userId, 10L)).thenReturn(true);

		List<PostResponseDTO> result = postService.getPostByUserId(userId);

		assertEquals(1, result.size());
		assertEquals(10L, result.get(0).getPostId());
		assertEquals("Hello", result.get(0).getCaption());
		assertEquals("image-url", result.get(0).getImageUrl());
		assertEquals(5L, result.get(0).getLikeCount());
		assertTrue(result.get(0).isLiked());

		verify(postRepository).findByUser_UserId(userId);
		verify(postLikeRepository).countByPost_PostId(10L);
		verify(postLikeRepository).existsByUser_UserIdAndPost_PostId(userId, 10L);
	}

	@Test
	void getPostByUserId_shouldThrowException_whenNoPostsExist() {

		Long userId = 1L;

		when(postRepository.findByUser_UserId(userId)).thenReturn(Optional.empty());

		assertThrows(PostNotFoundException.class, () -> postService.getPostByUserId(userId));

		verify(postLikeRepository, never()).countByPost_PostId(anyLong());
	}

	@Test
	void getFeedPosts_shouldReturnEmptyPage_whenFollowingListIsEmpty() {

		List<Long> followingIds = List.of();

		Page<PostResponseDTO> result = postService.getFeedPosts(followingIds, 0, 10, 1L);

		assertTrue(result.isEmpty());

		verify(postRepository, never()).findByUser_UserIdIn(anyList(), any(Pageable.class));
	}

	@Test
	void getFeedPosts_shouldReturnPosts_whenFollowingUsersExist() {

		List<Long> followingIds = List.of(2L, 3L);

		Post post = new Post();
		post.setPostId(10L);
		post.setCaption("Hello");

		Page<Post> postPage = new PageImpl<>(List.of(post));

		when(postRepository.findByUser_UserIdIn(eq(followingIds), any(Pageable.class))).thenReturn(postPage);

		when(postLikeRepository.countByPost_PostId(10L)).thenReturn(3L);

		when(postLikeRepository.existsByUser_UserIdAndPost_PostId(1L, 10L)).thenReturn(false);

		Page<PostResponseDTO> result = postService.getFeedPosts(followingIds, 0, 10, 1L);

		assertEquals(1, result.getTotalElements());
		assertEquals(10L, result.getContent().get(0).getPostId());
		assertEquals(3L, result.getContent().get(0).getLikeCount());
		assertFalse(result.getContent().get(0).isLiked());

		verify(postRepository).findByUser_UserIdIn(eq(followingIds), any(Pageable.class));
	}

	@Test
	void deletePostById_shouldDeletePostAndRelatedData_whenUserIsOwner() throws IOException {

		Long postId = 10L;

		User owner = new User();
		owner.setUserId(1L);

		Post post = new Post();
		post.setPostId(postId);
		post.setUser(owner);

		when(postRepository.findById(postId)).thenReturn(Optional.of(post));

		User currentUser = new User();
		currentUser.setUserId(1L);

		postService.deletePostById(postId, currentUser);

		verify(postLikeRepository).deleteByPost_PostId(postId);

		verify(commentRepository).deleteByPost_PostId(postId);

		verify(postRepository).delete(post);

		verify(cloudinaryService).deletePost(post);
	}

	@Test
	void deletePostById_shouldThrowException_whenPostDoesNotExist() throws IOException {

		Long postId = 10L;

		when(postRepository.findById(postId)).thenReturn(Optional.empty());

		User user = new User();

		assertThrows(PostNotFoundException.class, () -> postService.deletePostById(postId, user));

		verify(postRepository, never()).delete(any());
		verify(cloudinaryService, never()).deletePost(any());
	}

	@Test
	void deletePostById_shouldThrowException_whenUserIsNotOwner() throws IOException {

		User owner = new User();
		owner.setUserId(1L);

		Post post = new Post();
		post.setPostId(10L);
		post.setUser(owner);

		when(postRepository.findById(10L)).thenReturn(Optional.of(post));

		User anotherUser = new User();
		anotherUser.setUserId(2L);

		assertThrows(RuntimeException.class, () -> postService.deletePostById(10L, anotherUser));

		verify(postLikeRepository, never()).deleteByPost_PostId(anyLong());

		verify(commentRepository, never()).deleteByPost_PostId(anyLong());

		verify(postRepository, never()).delete(any());

		verify(cloudinaryService, never()).deletePost(any());
	}

	@Test
	void updatePostCaption_shouldUpdateCaption_whenUserIsOwner() {

		User owner = new User();
		owner.setUserId(1L);

		Post post = new Post();
		post.setPostId(10L);
		post.setUser(owner);

		UpdatePostRequestDTO dto = new UpdatePostRequestDTO();
		dto.setCaption("Updated caption");

		when(postRepository.findById(10L)).thenReturn(Optional.of(post));

		User currentUser = new User();
		currentUser.setUserId(1L);

		postService.updatePostCaption(10L, currentUser, dto);

		assertEquals("Updated caption", post.getCaption());

		verify(postRepository).save(post);
	}

	@Test
	void updatePostCaption_shouldThrowException_whenPostDoesNotExist() {

		when(postRepository.findById(10L)).thenReturn(Optional.empty());

		User user = new User();
		UpdatePostRequestDTO dto = new UpdatePostRequestDTO();

		assertThrows(PostNotFoundException.class, () -> postService.updatePostCaption(10L, user, dto));

		verify(postRepository, never()).save(any());
	}

	@Test
	void updatePostCaption_shouldThrowException_whenUserIsNotOwner() {

		User owner = new User();
		owner.setUserId(1L);

		Post post = new Post();
		post.setPostId(10L);
		post.setUser(owner);

		when(postRepository.findById(10L)).thenReturn(Optional.of(post));

		User anotherUser = new User();
		anotherUser.setUserId(2L);

		UpdatePostRequestDTO dto = new UpdatePostRequestDTO();
		dto.setCaption("Hacked caption");

		assertThrows(RuntimeException.class, () -> postService.updatePostCaption(10L, anotherUser, dto));

		verify(postRepository, never()).save(any());
	}

	@Test
	void getPostById_shouldReturnPost_whenPostExists() {

		Post post = new Post();
		post.setPostId(10L);

		when(postRepository.findById(10L)).thenReturn(Optional.of(post));

		Post result = postService.getPostById(10L);

		assertSame(post, result);

		verify(postRepository).findById(10L);
	}

	@Test
	void getPostById_shouldThrowException_whenPostDoesNotExist() {

		when(postRepository.findById(10L)).thenReturn(Optional.empty());

		assertThrows(PostNotFoundException.class, () -> postService.getPostById(10L));
	}
}