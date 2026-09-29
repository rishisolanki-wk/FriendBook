package com.friendbook.user;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UserProfileEditRequestDTO {

	private String profileBio;
	private AccountStatus accountStatus;
	private MultipartFile profileImage;
}
