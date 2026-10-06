package com.friendbook.posts;

import com.friendbook.comments.CommentRepository;
import com.friendbook.exception.PostNotFoundException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.friendbook.likes.PostLikeRepository;
import com.friendbook.user.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final CloudinaryService cloudinaryService;
	private final PostLikeRepository postLikeRepository;

	public String createPost(CreatePostRequestDTO createPostRequestDTO, User user) {
		try {
			CloudinaryUploadResult uploadResult = cloudinaryService.uploadImage(createPostRequestDTO.getImage());
			Post post = new Post();
			post.setCaption(createPostRequestDTO.getCaption());
			post.setCreatedAt(LocalDateTime.now());
			post.setUpdatedAt(LocalDateTime.now());
			post.setUser(user);
			post.setImageUrl(uploadResult.getImageUrl());
			post.setImagePublicId(uploadResult.getImagePublicId());
			postRepository.save(post);
			return "Image Uploaded Successfully";
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return "Image Not Uploaded!";
		}
	}

	public List<PostResponseDTO> getPostByUserId(Long id) {
		List<Post> posts = postRepository.findByUser_UserId(id)
				.orElseThrow(() -> new PostNotFoundException("No Posts Available!"));
		List<PostResponseDTO> dtos = new ArrayList<>();
		for (Post post : posts) {
			dtos.add(postResponseMapper(post, id));
		}
		return dtos;

	}

	private PostResponseDTO postResponseMapper(Post post, Long userId) {

		PostResponseDTO dto = new PostResponseDTO();

		dto.setCaption(post.getCaption());
		dto.setImageUrl(post.getImageUrl());
		dto.setPostId(post.getPostId());

		dto.setLikeCount(postLikeRepository.countByPost_PostId(post.getPostId()));

		dto.setLiked(postLikeRepository.existsByUser_UserIdAndPost_PostId(userId, post.getPostId()));

		dto.setUpdatedAt(post.getUpdatedAt());

		return dto;
	}

	public Page<PostResponseDTO> getFeedPosts(List<Long> followingIds, int page, int size, long userId) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		if (followingIds.isEmpty()) {
			return Page.empty(pageable);
		}
		Page<Post> posts = postRepository.findByUser_UserIdIn(followingIds, pageable);

		return posts.map(post -> postResponseMapper(post, userId));
	}

	@Transactional
	public void deletePostById(Long id, User user) throws IOException {
		Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post Not Found"));
		if (!post.getUser().getUserId().equals(user.getUserId())) {
			throw new RuntimeException("Not Authorized to delete the post!");
		}

		postLikeRepository.deleteByPost_PostId(id);
		commentRepository.deleteByPost_PostId(id);
		postRepository.delete(post);
		cloudinaryService.deletePost(post);
	}

	public void updatePostCaption(Long id, User user, UpdatePostRequestDTO dto) {
		Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post Not Found"));
		if (!post.getUser().getUserId().equals(user.getUserId())) {
			throw new RuntimeException("Not Authorized to delete the post!");
		}
		post.setCaption(dto.getCaption());
		postRepository.save(post);
	}

	public Post getPostById(Long postId) {
		return postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post Not Found!"));
	}

}
