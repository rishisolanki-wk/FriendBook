package com.friendbook.user;

import java.util.List;

import com.friendbook.posts.PostResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PublicProfileViewDTO {
	private Long userId;
	private String userName;
	private String firstName;
	private String lastName;
	private String profileImage;
	private String profileBio;
	private boolean activeStatus;
	private AccountStatus accountStatus;
	private List<PostResponseDTO> posts;
	private Long followers;
	private Long followings;
}