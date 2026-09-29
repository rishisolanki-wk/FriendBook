package com.friendbook.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileSearchResponseDTO {
	private Long userId;
	private String userName;
	private String firstName;
	private String lastName;
	private String profileImage;
}
