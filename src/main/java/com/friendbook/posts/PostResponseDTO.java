package com.friendbook.posts;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PostResponseDTO {

	private Long postId;
	private String caption;
	private String imageUrl;
	private LocalDateTime updatedAt;
}
