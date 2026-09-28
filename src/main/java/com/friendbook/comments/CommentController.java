package com.friendbook.comments;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.friendbook.user.User;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping("/create-comment/{postId}")
	public ResponseEntity<String> addComment(@PathVariable Long postId,
			@RequestBody CommentRequestDTO commentRequestDTO, Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		commentService.addCommentByPostId(postId, commentRequestDTO, user);
		return ResponseEntity.status(HttpStatus.CREATED).body("Comment Added");
	}

	@GetMapping("/view-comments/{postId}")
	public ResponseEntity<List<CommentResponseDTO>> getAllCommentByPostId(@PathVariable("postId") Long postId) {
		return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentsByPostId(postId));
	}

	@DeleteMapping("/remove-comment/{commentId}")
	public ResponseEntity<String> removeComment(@PathVariable("commentId") Long commentId,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		commentService.removeCommentById(commentId, user);
		return ResponseEntity.status(HttpStatus.OK).body("Comment Deleted!");
	}
}
