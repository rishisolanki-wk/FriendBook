package com.friendbook.user;

import java.util.List;

import com.friendbook.posts.PostResponseDTO;

import lombok.Data;

@Data
public class UserFeedPostsDTO {
	private List<PostResponseDTO> posts;
}
