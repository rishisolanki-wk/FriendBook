package com.friendbook.likes;

import java.util.List;

import lombok.Data;

@Data
public class ViewPostLikesDTO {
	private Long postId;
	private List<String> userName;
	private Long count;
}
