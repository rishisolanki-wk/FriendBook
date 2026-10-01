package com.friendbook.comments;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.friendbook.exception.PostNotFoundException;
import com.friendbook.exception.ResourceNotFoundException;
import com.friendbook.notifications.NotificationService;
import com.friendbook.notifications.NotificationType;
import com.friendbook.posts.Post;
import com.friendbook.posts.PostRepository;
import com.friendbook.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final NotificationService notificationService;

	public void addCommentByPostId(Long postId, CommentRequestDTO commentRequestDTO, User user) {
		if (commentRepository.existsByUser_UserIdAndPost_PostId(user.getUserId(), postId)) {
			return;
		}
		Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post Not Found!"));
		Comment comment = new Comment();
		comment.setCommentMsg(commentRequestDTO.getComment());
		comment.setCreatedAt(LocalDateTime.now());
		comment.setPost(post);
		comment.setUser(user);
		commentRepository.save(comment);
		notificationService.createNotification(post.getUser(), user, NotificationType.COMMENT, post);
	}

	public List<CommentResponseDTO> getCommentsByPostId(Long postId) {
		List<Comment> comments = commentRepository.findByPost_PostId(postId)
				.orElseThrow(() -> new PostNotFoundException("Post Not Found!"));
		return commentMapper(comments);
	}

	private List<CommentResponseDTO> commentMapper(List<Comment> comments) {
		List<CommentResponseDTO> repDto = new ArrayList<>();
		for (Comment comment : comments) {
			CommentResponseDTO commentResponseDTO = new CommentResponseDTO();
			commentResponseDTO.setCommentMsg(comment.getCommentMsg());
			commentResponseDTO.setCreatedAt(comment.getCreatedAt());
			commentResponseDTO.setCommentId(comment.getCommentId());
			commentResponseDTO.setPost(comment.getPost());
			commentResponseDTO.setUser(comment.getUser());
			repDto.add(commentResponseDTO);
		}
		return repDto;
	}

	public void removeCommentById(Long commentId, User user) {
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new ResourceNotFoundException("Comment Not Found!"));
		if (!comment.getUser().getUserId().equals(user.getUserId())) {
			return;
		}
		commentRepository.delete(comment);
	}

}
