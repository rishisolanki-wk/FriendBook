package com.friendbook.posts;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PostResponseDTO {

	private Long postId;
	private String caption;
	private String imageUrl;
	private Long likeCount;
	private boolean isLiked;
	private LocalDateTime updatedAt;
}
