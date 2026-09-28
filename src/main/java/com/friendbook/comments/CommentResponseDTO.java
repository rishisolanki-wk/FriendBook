package com.friendbook.comments;

import java.time.LocalDateTime;

import com.friendbook.posts.Post;
import com.friendbook.user.User;

import lombok.Data;

@Data
public class CommentResponseDTO {
	private Long commentId;
	private String commentMsg;
	private User user;
	private Post post;
	private LocalDateTime createdAt;
}
