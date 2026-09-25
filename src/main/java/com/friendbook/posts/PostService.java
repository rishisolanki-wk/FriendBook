package com.friendbook.posts;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.friendbook.user.User;
import com.friendbook.user.UserFeedPostsDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final CloudinaryService cloudinaryService;

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

	public List<PostResponseDTO> getPostByUserId(Long Id) {
		List<Post> posts = postRepository.findByUser_UserId(Id)
				.orElseThrow(() -> new RuntimeException("No Posts Available!"));
		return postResponseMapper(posts);

	}

	private List<PostResponseDTO> postResponseMapper(List<Post> posts) {
		List<PostResponseDTO> dtos = new ArrayList<>();
		for (Post post : posts) {
			PostResponseDTO dto = new PostResponseDTO();
			dto.setCaption(post.getCaption());
			dto.setImageUrl(post.getImageUrl());
			dto.setPostId(post.getPostId());
			dto.setImagePublicId(post.getImagePublicId());
			dto.setUpdatedAt(post.getUpdatedAt());
			dtos.add(dto);
		}
		return dtos;
	}

	public UserFeedPostsDTO getFeedPosts() {
		UserFeedPostsDTO dto = new UserFeedPostsDTO();
		dto.setPosts(postResponseMapper(postRepository.findAll()));
		return dto;
	}

	public void deletePostById(Long id, User user) throws IOException {
		Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post Not Found"));
		if (post.getUser().getUserId() != user.getUserId()) {
			throw new RuntimeException("Not Authorized to delete the post!");
		}
		cloudinaryService.deletePost(post);
		postRepository.delete(post);
	}

	public void updatePostCaption(Long id, User user, UpdatePostRequestDTO dto) {
		Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post Not Found"));
		if (post.getUser().getUserId() != user.getUserId()) {
			throw new RuntimeException("Not Authorized to delete the post!");
		}
		post.setCaption(dto.getCaption());
		postRepository.save(post);
	}

}
