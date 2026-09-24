package com.friendbook.posts;

import java.time.LocalDateTime;

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

}
