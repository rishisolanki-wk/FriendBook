package com.friendbook.posts;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CreatePostRequestDTO {
	private String caption;
	private MultipartFile image;
}
