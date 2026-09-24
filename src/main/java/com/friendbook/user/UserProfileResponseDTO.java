package com.friendbook.user;

import java.time.LocalDate;
import java.util.List;

import com.friendbook.posts.PostResponseDTO;

import lombok.Data;

@Data
public class UserProfileResponseDTO {

	private String userName;
	private String firstName;
	private String lastName;
	private String email;
	private String mobile;
	private Gender gender;
	private List<PostResponseDTO> posts;
	private LocalDate createdAt;
}
