package com.friendbook.posts;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.friendbook.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final CloudinaryService cloudinaryService;

	public String createPost(CreatePostRequestDTO createPostRequestDTO, User user) {
		try {
			Post post = new Post();
			post.setCaption(createPostRequestDTO.getCaption());
			post.setCreatedAt(LocalDateTime.now());
			post.setUpdatedAt(LocalDateTime.now());
			post.setUser(user);
			post.setImageUrl(cloudinaryService.uploadImage(createPostRequestDTO.getImage()));
			postRepository.save(post);
			return "Image Uploaded Successfully";
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return null;
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
			dto.setUpdatedAt(post.getUpdatedAt());
			dtos.add(dto);
		}
		return dtos;
	}
}
