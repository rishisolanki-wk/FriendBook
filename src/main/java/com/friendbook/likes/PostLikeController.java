package com.friendbook.likes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.friendbook.user.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class PostLikeController {
	private final PostLikeService postLikeService;

	@PostMapping("/{postId}")
	public ResponseEntity<String> postLikedByUser(@PathVariable("postId") Long postId, Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		postLikeService.addPostLike(postId, user);
		return ResponseEntity.status(HttpStatus.OK).body("Liked Successfully");
	}

	@DeleteMapping("/{postId}")
	public ResponseEntity<String> unlikePostByUser(@PathVariable("postId") Long postId, Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		postLikeService.unlikePost(postId, user);
		return ResponseEntity.status(HttpStatus.OK).body("Like Removed Successfully");
	}
}
