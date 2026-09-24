package com.friendbook.posts;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.friendbook.user.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

	private final PostService postService;

	@PostMapping("/create-post")
	public ResponseEntity<String> createPost(@ModelAttribute CreatePostRequestDTO createPostRequestDTO,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(createPostRequestDTO, user));

	}

	@DeleteMapping("/delete-post/{id}")
	public ResponseEntity<String> deletePost(@PathVariable("id") Long id, Authentication authentication) {
		try {
			User user = (User) authentication.getPrincipal();
			postService.deletePostById(id, user);
			return ResponseEntity.status(HttpStatus.OK).body("Post Deleted Successfully");
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Post Not Deleted");
		}
	}
}
